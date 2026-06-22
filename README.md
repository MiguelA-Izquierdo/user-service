# User Service

A user management microservice with JWT authentication, built with **Spring Boot 3.4** and **Java 17**. It follows **Domain-Driven Design (DDD)** principles with a clear separation between domain, application, and infrastructure layers.

---

## Tech Stack

| Technology | Version            |
|---|--------------------|
| Java | 17                 |
| Spring Boot | 3.4.13             |
| Spring Security | (included in Boot) |
| Spring Data JPA | (included in Boot) |
| MySQL | -                  |
| RabbitMQ (AMQP) | -                  |
| JWT (jjwt) | 0.11.5             |
| Swagger / OpenAPI | springdoc 2.8.1    |
| Bucket4j | 8.10.1             |
| Caffeine | 3.1.8              |
| JaCoCo (coverage) | 0.8.12             |
| Mockito | 5.5.0              |

---

## Architecture

The project is organized into three main modules following DDD:

```
src/main/java/com/app/userService/
├── _shared/          # Cross-cutting concerns (events, exceptions, security, DTOs)
├── auth/             # Authentication module
│   ├── domain/       # Models, domain services, value objects
│   ├── application/  # Use cases, commands, queries, validators
│   └── infrastructure/ # REST controllers, JPA repositories, JWT services
└── user/             # User module
    ├── domain/
    ├── application/
    └── infrastructure/
```

Each module exposes its logic through a **CommandBus** and a **QueryBus**, separating write and read operations (lightweight CQRS).

For the reasoning behind key design choices (outbox pattern, per-user JWT secret, rate limiting, DLQ) see **[docs/architecture.md](docs/architecture.md)**.

### Security

- **JWT-based** authentication.
- Each user holds their own `secretKey`, allowing individual session invalidation without affecting other users.
- Account locking with unlock via a reset token.

#### Roles and permissions

| Role | Permissions |
|---|---|
| `ROLE_USER` | Read, update, and delete **their own** profile. Change their own password. Logout themselves. |
| `ROLE_ADMIN` | Everything a user can do on **any** user. Create users (`POST /users`). List all users (`GET /users`). Access Swagger UI. |
| `ROLE_SUPER_ADMIN` | Everything an admin can do. Grant the super admin role to another user (`POST /users/grant-role-super-admin/{userId}`). Access health endpoint details. |

---

## API Documentation

Full interactive documentation is available at `/swagger-ui.html` once the service is running.

> **Access requires `ROLE_ADMIN` or `ROLE_SUPER_ADMIN`.** Authenticate via `POST /auth` and include the returned token as `Authorization: Bearer <token>` in Swagger's "Authorize" dialog.

For key flows (account lock, password reset, login, logout) see **[docs/flows.md](docs/flows.md)**.

---

## Deployment

| Mode | Prerequisites | Guide |
|---|---|---|
| Docker Compose | Docker | [docs/deployment.md → Docker Compose](docs/deployment.md#docker-compose-recommended) |
| Local (no Docker) | Java 17, Maven, MySQL, RabbitMQ | [docs/deployment.md → Local development](docs/deployment.md#local-development-without-docker) |

**Docker Compose quick-start:**

```bash
cp .env.example .env.docker   # fill in secrets
docker compose --env-file .env.docker up --build
```

**Local quick-start:**

```bash
cp .env.example .env          # fill in local values
./mvnw spring-boot:run
```

See **[docs/deployment.md](docs/deployment.md)** for the full guide: environment variable reference, first login, and admin user setup.

---

## Tests

```bash
./mvnw test
```

To generate a JaCoCo coverage report:

```bash
./mvnw verify
# Report available at: target/site/jacoco/index.html
```

---

## Domain Events

All events are published to the **`userExchange`** exchange (topic type).

| Event type | Routing key | Queue | Trigger | Payload |
|---|---|---|---|---|
| `user.created` | `user.created` | `userCreatedQueue` | User created | `userId`, `name`, `lastName`, `email` |
| `user.deleted` | `user.deleted` | `userDeletedQueue` | User deleted | `userId`, `name`, `lastName`, `email` |
| `user.logged.with.token` | `user.logged.with.token` | `userLoggedQueue` | Login via existing JWT | `userId`, `name`, `lastName`, `email` |
| `user.logged.without.token` | `user.logged.without.token` | `userLoggedQueue` | Login via credentials | `userId`, `name`, `lastName`, `email` |
| `user.logout` | `user.logged.logout` | `userLoggedQueue` | User logout | `userId`, `name`, `lastName`, `email` |
| `user.locked` | `user.locked` | `userLockedQueue` | Account locked | `userId`, `name`, `lastName`, `email`, `token`, `expirationDate` |
| `user.unlocked` | `user.unlocked` | `userUnlockedQueue` | Account unlocked | `userId`, `name`, `lastName`, `email` |

> To subscribe to all login/logout activity use the binding pattern `user.logged.#`.

---

## License

Released under the [MIT License](LICENSE) © 2026 Miguel A. Izquierdo.