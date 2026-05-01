# Architecture decisions

## Rate limiting

Rate limiting is **not implemented in this service**. It is delegated entirely to the API gateway that sits in front of it.

**Why:** Enforcing rate limits at the gateway keeps the policy in one place, avoids duplicating logic across services, and allows tuning limits without redeploying individual services.

**Implication:** This service must always be deployed behind the gateway. Direct exposure to the internet without a gateway in front is not a supported configuration.

**Exception — login brute-force protection:** If per-user login attempt limiting is needed (e.g. lock an account after N failed attempts), that logic belongs here because the gateway has no access to user identity before authentication. Implement it at the service level, not the gateway level.

---

## Transactional outbox for event publishing

Domain events are **not published directly to RabbitMQ** at the moment they occur. Instead:

1. The event payload is written to an `outbox` table within the same database transaction as the business operation.
2. A scheduled job (`OutboxEventPublisher`) polls the table every **5 seconds**, publishes pending events to RabbitMQ, and marks them as processed.

**Why:** Publishing directly to a message broker inside a business transaction creates a two-phase commit problem — the DB write and the broker publish can diverge if either fails. The outbox pattern keeps the event durable in the same transaction as the state change, so events are never silently lost.

**Implication:** Events are delivered with a latency of up to ~5 seconds after the triggering operation. This is intentional and acceptable for the current use cases. If an event fails to publish it is marked `FAILED` in the outbox table and is not retried automatically — a manual or tooling-assisted reprocess is required.

---

## Per-user JWT secret key

Each user record stores its own `secretKey`. Every JWT issued for a user is signed with that user's key.

**Why:** This allows **instant session invalidation** for a single user without maintaining a token blacklist. Rotating the key (on logout or password reset) immediately invalidates all previously issued tokens for that user, and only for that user — other sessions are unaffected.

**Where it is used:**
- `POST /auth/logout` and `POST /auth/logout/{userId}` — rotates the target user's key.
- `POST /auth/unlock-reset-password` — rotates the key as part of the password reset flow, ensuring any JWT obtained before the reset is invalid.

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