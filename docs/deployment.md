# Deployment

## Docker Compose (recommended)

The fastest way to run the full stack locally.

**1. Create your environment file:**

```bash
cp .env.example .env.docker
```

Edit `.env.docker` with the values you want (the defaults work for local development).

**2. Start the stack:**

```bash
docker compose --env-file .env.docker up --build
```

This starts three containers:

| Container | Description | Host port → Container port |
|---|---|---|
| `user-service` | Spring Boot application | `8081 → 8080` |
| `user-service-db` | MySQL 8.0 | `3306 → 3306` |
| `user-service-rabbitmq` | RabbitMQ | `5672 → 5672` / `15672 → 15672` (management UI) |

The application waits for MySQL and RabbitMQ to pass their health checks before starting. Database data is persisted in a Docker volume (`mysql_data`) across restarts.

> `.env.docker` is listed in `.gitignore` and will never be committed.

### First-time setup: admin user

The service automatically creates a super admin user on first startup if one does not already exist. No manual steps required.

The admin credentials are read from environment variables defined in `.env.docker`:

| Variable | Description |
|---|---|
| `ADMIN_EMAIL` | Admin account email |
| `ADMIN_PASSWORD` | Admin account password |
| `ADMIN_NAME` | Admin first name |
| `ADMIN_LAST_NAME` | Admin last name |

### First login

With the stack running, authenticate using the admin credentials from `.env.docker`:

```bash
curl -X POST http://localhost:8081/auth \
  -H "Content-Type: application/json" \
  -d '{"email": "<ADMIN_EMAIL>", "password": "<ADMIN_PASSWORD>"}'
```

The response includes a `token` field. Use it to call any protected endpoint:

```bash
curl http://localhost:8081/users \
  -H "Authorization: Bearer <token>"
```

---

## Local development (without Docker)

**Prerequisites:** Java 17, Maven, a running MySQL instance, a running RabbitMQ instance.

**1. Create your environment file:**

```bash
cp .env.example .env
```

Edit `.env` with your local database and RabbitMQ connection details.

**2. Run the application:**

```bash
./mvnw spring-boot:run
```

The service starts at `http://localhost:8080`.

---

## Environment variables reference

| Variable | Description | Required | Default |
|---|---|---|---|
| `DB_URL` | Full JDBC connection URL | Yes | — |
| `DB_USER` | Database username | Yes | — |
| `DB_PASSWORD` | Database password | Yes | — |
| `JWT_SECRET` | Secret key for signing JWTs (min. 32 chars) | Yes | — |
| `RABBITMQ_HOST` | RabbitMQ host | Yes | — |
| `RABBITMQ_PORT` | RabbitMQ port | Yes | — |
| `RABBITMQ_USERNAME` | RabbitMQ username | Yes | — |
| `RABBITMQ_PASSWORD` | RabbitMQ password | Yes | — |
| `CORS_ALLOWED_ORIGINS` | Allowed CORS origins | No | `http://localhost:4200` |
| `RATE_LIMIT_BACKEND` | Rate limit backend: `memory` or `redis` | No | `memory` |
| `RATE_LIMIT_TRUST_PROXY` | Trust `X-Forwarded-For` header for client IP (enable only behind a trusted proxy) | No | `false` |
| `RATE_LIMIT_REDIS_HEALTH` | Let the Redis health indicator affect `/actuator/health` (and thus readiness). Keep `false` unless Redis is the active backend | No | `false` |
| `REDIS_HOST` | Redis host (required when `RATE_LIMIT_BACKEND=redis`) | No | `localhost` |
| `REDIS_PORT` | Redis port | No | `6379` |
| `APP_BASE_URL` | Public-facing base URL (used in pagination links) | No | `http://localhost:8080` |
| `ADMIN_EMAIL` | Email for the auto-created super admin | Yes | — |
| `ADMIN_PASSWORD` | Password for the auto-created super admin | Yes | — |
| `ADMIN_NAME` | First name for the auto-created super admin | Yes | — |
| `ADMIN_LAST_NAME` | Last name for the auto-created super admin | Yes | — |

---

## Rate limiting and Redis (optional dependency)

Rate limiting has two interchangeable backends, selected with `RATE_LIMIT_BACKEND`. **Redis is optional** — the service runs perfectly without it.

| Environment | `RATE_LIMIT_BACKEND` | Redis deployed? | Notes |
|---|---|---|---|
| Local / single instance | `memory` (default) | No | In-process Caffeine + Bucket4j. Nothing else to run. |
| Horizontally scaled (k8s, multiple replicas) | `redis` | Yes | Shared counter across replicas. Activate the `redis` Spring profile. |

### Activating the Redis backend

Set either the environment variable or the Spring profile (the profile also re-enables the Redis health indicator):

```bash
# Option A — environment variable
RATE_LIMIT_BACKEND=redis
RATE_LIMIT_REDIS_HEALTH=true
REDIS_HOST=my-redis
REDIS_PORT=6379

# Option B — Spring profile (sets both of the above defaults for you)
SPRING_PROFILES_ACTIVE=redis
```

`application-redis.properties` bundles `rate-limit.backend=redis` + `management.health.redis.enabled=true`.

### Behaviour when Redis is down

With the `redis` backend, the Redis client is wrapped by `ResilientRateLimitBackend`. If Redis becomes unreachable at request time, the service **degrades to a per-instance in-memory limit** instead of removing rate limiting — sensitive endpoints like `POST /auth` stay protected during the outage. A Redis outage never blocks legitimate users.

### Why `RATE_LIMIT_REDIS_HEALTH` matters

Spring Boot auto-registers a Redis health indicator that pings Redis as part of `/actuator/health`. In a `memory`-backend environment that doesn't even run Redis, this would flip health to `DOWN` and **fail your Kubernetes readiness probe** — the pod would never receive traffic. The indicator is therefore **disabled by default** and only enabled where Redis is a real dependency (the `redis` profile sets `RATE_LIMIT_REDIS_HEALTH=true`). Leave it `false` everywhere else.

---

## Observability: Actuator, health checks & Kubernetes

The service exposes Spring Boot Actuator. By default **only `health`** is exposed, with full details restricted to `SUPER_ADMIN` (`management.endpoint.health.show-details=when-authorized`).

### Exposing Actuator securely (internal only, not to the public)

The goal is to make health/metrics reachable by Kubernetes (probes) and Prometheus (scraping) **without** exposing them to external clients. The robust way is **network isolation, not application auth** — run Actuator on a separate management port that the public Ingress never routes to.

**1. Move Actuator to its own port:**

```properties
server.port=8080            # public API — the only port the Ingress targets
management.server.port=8081 # Actuator — never published externally
management.endpoints.web.exposure.include=health,info,prometheus
management.endpoint.health.probes.enabled=true
```

> When `management.server.port` differs from `server.port`, Actuator runs in a separate management context. The application's JWT `SecurityFilterChain` is bound to the main context and does **not** apply to `:8081`. That is intentional here — protection comes from the network, not from app-level auth. Make sure `:8081` is never reachable from outside the cluster.

**2. Update the Dockerfile to document the management port** (`EXPOSE` is metadata only; the app must still be configured as above):

```dockerfile
EXPOSE 8080 8081
```

**3. Point Kubernetes probes at the management port** (the kubelet reaches the Pod IP directly, never through the Ingress):

```yaml
ports:
  - name: http
    containerPort: 8080
  - name: management
    containerPort: 8081      # declared, but NOT added to the public Service

livenessProbe:
  httpGet:
    path: /actuator/health/liveness
    port: 8081
  initialDelaySeconds: 30
  periodSeconds: 10
readinessProbe:
  httpGet:
    path: /actuator/health/readiness
    port: 8081
  periodSeconds: 5
```

**4. The public Service / Ingress targets only `8080`:**

```yaml
apiVersion: v1
kind: Service
metadata:
  name: user-service
spec:
  ports:
    - name: http
      port: 80
      targetPort: 8080        # 8081 is intentionally absent
```

**5. (Optional) Restrict `:8081` to Prometheus with a NetworkPolicy** — kubelet probes are unaffected by NetworkPolicies since they originate from the node:

```yaml
apiVersion: networking.k8s.io/v1
kind: NetworkPolicy
metadata:
  name: user-service-mgmt
spec:
  podSelector:
    matchLabels: { app: user-service }
  policyTypes: [Ingress]
  ingress:
    - from:
        - namespaceSelector:
            matchLabels: { name: monitoring }
      ports:
        - { protocol: TCP, port: 8081 }
```

**Rule of thumb:** infrastructure traffic (probes, metrics) is isolated by network; application auth is for users. Never expose `/actuator` on the public port and rely on a filter to protect it.

---

## API documentation

Interactive Swagger UI is available at `/swagger-ui.html` once the service is running.