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
| `APP_BASE_URL` | Public-facing base URL (used in pagination links) | No | `http://localhost:8080` |
| `ADMIN_EMAIL` | Email for the auto-created super admin | Yes | — |
| `ADMIN_PASSWORD` | Password for the auto-created super admin | Yes | — |
| `ADMIN_NAME` | First name for the auto-created super admin | Yes | — |
| `ADMIN_LAST_NAME` | Last name for the auto-created super admin | Yes | — |

---

## API documentation

Interactive Swagger UI is available at `/swagger-ui.html` once the service is running.