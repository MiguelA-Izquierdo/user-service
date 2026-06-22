# Architecture decisions

## Rate limiting

Rate limiting is enforced at the **servlet filter level**, before Spring Security processes the request. This protects unauthenticated endpoints from brute-force and enumeration attacks.

### Protected endpoints

| Endpoint | Limit | Window | Reason |
|---|---|---|---|
| `POST /auth` | 5 requests | 1 minute | Login brute-force |
| `POST /auth/unlock-reset-password` | 3 requests | 5 minutes | Token brute-force |

Requests that exceed the limit receive `429 Too Many Requests` with a `Retry-After` header indicating how many seconds to wait.

### Backends

The backend is selected via the `RATE_LIMIT_BACKEND` environment variable:

| Value | Implementation | Use case |
|---|---|---|
| `memory` (default) | Caffeine + Bucket4j (in-process) | Single instance or development |
| `redis` | Lua `INCR/EXPIRE` on Redis, wrapped by `ResilientRateLimitBackend` | Horizontally scaled deployments |

**Why two backends:** An in-process cache (Caffeine) gives each instance its own independent counter — in a horizontally scaled deployment with N instances, the effective limit would be N × the configured limit, defeating the purpose. Redis solves this by sharing a single counter across all instances. The Lua script used for the Redis backend is atomic, so there are no race conditions between instances.

**Graceful degradation, not blind fail-open:** When `RATE_LIMIT_BACKEND=redis`, the Redis backend is wrapped by `ResilientRateLimitBackend`. If a Redis call fails (outage, timeout), the wrapper does **not** simply let every request through — it falls back to a per-instance in-memory limit for that request. This keeps brute-force protection on `/auth` alive during a Redis outage (degraded to N × limit across N instances), which is far safer than removing the limit entirely. A Redis outage still never blocks legitimate users or takes down authentication.

> The raw `RedisRateLimitBackend.tryConsume` still fails open on its own (returns `true` on error); the resilient wrapper uses `tryConsumeOrThrow` so it can observe the failure and decide to degrade. The standalone fail-open path remains for direct/standalone use.

### Redis as an optional dependency

Redis is only required when `RATE_LIMIT_BACKEND=redis`. Because the `spring-boot-starter-data-redis` starter is always on the classpath, Spring Boot would normally register a Redis `HealthIndicator` that pings Redis and flips `/actuator/health` to `DOWN` when Redis is absent — which would break Kubernetes readiness probes in `memory`-backend environments that don't even deploy Redis.

To prevent that, the Redis health indicator is **disabled by default** and gated behind a flag:

```properties
management.health.redis.enabled=${RATE_LIMIT_REDIS_HEALTH:false}
```

The `redis` Spring profile (`application-redis.properties`) sets `RATE_LIMIT_BACKEND=redis` **and** re-enables the indicator (`management.health.redis.enabled=true`), so Redis health only counts toward readiness in environments where Redis is actually a dependency.

### IP extraction and proxy trust

The filter uses `request.getRemoteAddr()` as the client identifier by default.

If the service is deployed behind a trusted reverse proxy or load balancer that sets the `X-Forwarded-For` header, set `RATE_LIMIT_TRUST_PROXY=true`. This makes the filter read the first IP from the header (the original client IP) instead of the proxy's IP.

**Do not enable `RATE_LIMIT_TRUST_PROXY` unless there is a trusted proxy in front of the service.** When enabled without a proxy, any client can spoof a different IP on every request by setting their own `X-Forwarded-For` header, bypassing rate limiting entirely.

---

## Transactional outbox for event publishing

Domain events are **not published directly to RabbitMQ** at the moment they occur. Instead:

1. The event payload is written to the `outbox_events` table within the same database transaction as the business operation. The row's ID is the domain event's own `eventId`, so retrying a publish never creates a duplicate outbox row (idempotency).
2. A scheduled job (`OutboxEventPublisher`) polls the table every **5 seconds**, claims a batch of publishable rows, publishes them to RabbitMQ, and marks each as processed.

**Why:** Publishing directly to a message broker inside a business transaction creates a two-phase commit problem — the DB write and the broker publish can diverge if either fails. The outbox pattern keeps the event durable in the same transaction as the state change, so events are never silently lost.

### Concurrency-safe polling

The poller runs `fetchPublishableBatch` inside a single transaction with `SELECT ... FOR UPDATE SKIP LOCKED` (via the JPA `PESSIMISTIC_WRITE` lock plus a `lock.timeout = -2` hint that maps to Hibernate's `SKIP_LOCKED`). Row-level locks are held until commit, so when multiple instances poll concurrently each one claims a **disjoint** set of rows instead of double-publishing the same events. Requires MySQL 8+ (or another `SKIP LOCKED`-capable engine).

A row is *publishable* when its status is `PENDING` or `FAILED` **and** `next_retry_at` is null or already elapsed. Rows are processed oldest-first (`ORDER BY created_at`). The poll predicate is covered by the `idx_outbox_events_poll (status, next_retry_at)` index.

### Retry with exponential backoff and a dead-letter state

A row moves through the `OutboxEventStatus` lifecycle:

| Status | Meaning | Picked up by poller? |
|---|---|---|
| `PENDING` | Never attempted (initial state). | Yes |
| `FAILED` | Transient publish failure; a retry is scheduled at `next_retry_at`. | Yes, once `next_retry_at` has elapsed |
| `PROCESSED` | Published successfully. Terminal. | No |
| `DEAD` | Retry budget exhausted. Terminal. | No |

On a failed publish, `registerFailure` increments the attempt counter and either:

- **schedules a retry** (status `FAILED`) with exponential backoff — `base × 2^(attempt-1)`, i.e. with the default 10s base: 10s, 20s, 40s, 80s…; or
- **parks the event as `DEAD`** once `max-attempts` is reached, so the poller stops picking it up.

Tunable via configuration (defaults shown):

```properties
outbox.publisher.poll-interval-ms=5000   # how often the poller runs
outbox.publisher.batch-size=100          # rows claimed per poll
outbox.publisher.max-attempts=5          # attempts before a row is parked as DEAD
outbox.publisher.backoff-seconds=10      # exponential backoff base
```

**Implications:**
- Events are delivered with a latency of up to ~5 seconds (one poll interval) after the triggering operation. This is intentional and acceptable for the current use cases.
- Transient broker failures recover automatically through the backoff retries — no manual action is needed for them.
- A `DEAD` event is terminal: it requires a manual or tooling-assisted reprocess (e.g. resetting its status to `PENDING` after the root cause is fixed). Monitoring the count of `DEAD` rows is a good operational alert.

---

## Per-user JWT secret key

Each user record stores its own `secretKey`. Every JWT issued for a user is signed with that user's key.

**Why:** This allows **instant session invalidation** for a single user without maintaining a token blacklist. Rotating the key (on logout or password reset) immediately invalidates all previously issued tokens for that user, and only for that user — other sessions are unaffected.

**Where it is used:**
- `POST /auth/logout` and `POST /auth/logout/{userId}` — rotates the target user's key.
- `POST /auth/unlock-reset-password` — rotates the key as part of the password reset flow, ensuring any JWT obtained before the reset is invalid.

---

## Token introspection for internal services

Other services in the same cluster that need to verify a user's identity or check admin privileges call `GET /auth/introspect` with the user's Bearer token. The response is:

```json
{
  "sub": "<userId>",
  "roles": ["ROLE_ADMIN"],
  "isAdmin": true
}
```

`401` means the token is invalid or expired; `200` means it is valid and the fields can be trusted.

**Why an endpoint instead of shared-secret offline validation:** JWTs in this service are signed with a per-user secret (`generalSecret + user.secretKey`). No external service can validate a token offline without access to that user record, so a network call to this service is unavoidable.

**Access control:** The endpoint has no ingress rule — it is reachable only from within the cluster via `ClusterIP`. It still requires a valid Bearer token, so an attacker who somehow reaches it still cannot probe arbitrary tokens without a valid one. If the service is later exposed publicly, this endpoint is no worse than any other authenticated endpoint — the JWT requirement provides the same baseline protection throughout.

**Rate limiting:** `GET /auth/introspect` is intentionally not rate limited at the application level — this endpoint is designed for cluster-internal use where callers are trusted services. If the service is ever exposed via an ingress, rate limiting for this endpoint should be applied at the ingress/gateway level rather than in the application.

---

## Dead-letter queues (DLQ)

Every RabbitMQ queue has a corresponding dead-letter queue:

| Queue | DLQ |
|---|---|
| `userCreatedQueue` | `userCreatedQueue.dlq` |
| `userDeletedQueue` | `userDeletedQueue.dlq` |
| `userLockedQueue` | `userLockedQueue.dlq` |
| `userLoggedQueue` | `userLoggedQueue.dlq` |

All DLQs are bound to the `user.dlx` dead-letter exchange.

**Why:** Messages that fail processing on the consumer side are routed to the DLQ instead of being discarded. This preserves the event for manual inspection and replay without blocking the main queue.

**Implication:** DLQ monitoring and replay tooling is the responsibility of whoever operates the broader platform, not this service.

---

## CQRS via CommandBus and QueryBus

Write operations (create, update, delete, logout, grant role) are dispatched through a `CommandBus`. Read operations (get user, list users, login) go through a `QueryBus`.

**Why:** Separating the write and read paths makes each handler easier to reason about in isolation, prevents accidental side effects in query handlers, and leaves room to split read and write data sources in the future if needed.

**Current scope:** Both buses are in-process — there is no separate read model or replica. The split is logical, not physical.