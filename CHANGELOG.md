# Changelog

All notable changes to this project are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2026-06-22

### Added

#### Authentication & Security
- JWT-based authentication with per-user `secretKey`, enabling individual session invalidation without affecting other users.
- Account locking after repeated failed login attempts, with unlock via a one-time reset token.
- Role-based access control with three roles: `ROLE_USER`, `ROLE_ADMIN`, and `ROLE_SUPER_ADMIN`.
- Rate limiting on sensitive endpoints with two interchangeable backends: in-memory (Caffeine + Bucket4j, default) and distributed (Redis). The Redis backend degrades gracefully to in-memory limiting if Redis becomes unreachable.
- Security headers: `X-Frame-Options: DENY`, `Content-Security-Policy`, `Referrer-Policy`, and optional `Strict-Transport-Security` (`SECURITY_HSTS_ENABLED`).

#### User Management
- Full CRUD for users (`POST`, `GET`, `PUT`, `DELETE /users`).
- Pagination on `GET /users` with configurable page size and navigation links.
- Password change endpoint for authenticated users.
- Super-admin role grant endpoint (`POST /users/grant-role-super-admin/{userId}`), restricted to `ROLE_SUPER_ADMIN`.
- Automatic super-admin seed user on first startup, configured via environment variables (`ADMIN_EMAIL`, `ADMIN_PASSWORD`, `ADMIN_NAME`, `ADMIN_LAST_NAME`).

#### Domain Events & Messaging
- Outbox pattern for reliable event publishing: events are persisted to the `outbox_events` table within the same transaction and published to RabbitMQ asynchronously.
- Dead-letter queue (DLQ) per event type for automatic retry and failure isolation.
- Seven domain events published to the `userExchange` topic exchange:

| Event | Routing key | Trigger |
|---|---|---|
| `user.created` | `user.created` | User created |
| `user.deleted` | `user.deleted` | User deleted |
| `user.logged.with.token` | `user.logged.with.token` | Login via existing JWT |
| `user.logged.without.token` | `user.logged.without.token` | Login via credentials |
| `user.logout` | `user.logged.logout` | User logout |
| `user.locked` | `user.locked` | Account locked |
| `user.unlocked` | `user.unlocked` | Account unlocked |

#### Infrastructure
- Flyway-managed schema migrations (`V1__initial_schema.sql`): `users`, `user_roles`, `password_reset_tokens`, `outbox_events`, `user_action_log`.
- User action audit log persisted to `user_action_log` for every relevant operation.
- Spring Boot Actuator on a dedicated management port (`MANAGEMENT_PORT`, default `8081`), isolated from the public API port and its JWT `SecurityFilterChain`. Only `health` and `info` endpoints exposed; health details restricted to `ROLE_SUPER_ADMIN`.
- Liveness and readiness probes enabled (`/actuator/health/liveness`, `/actuator/health/readiness`).
- Docker Compose stack with MySQL 8.0, RabbitMQ 3 (with management UI), health checks, and per-service resource limits.
- Multi-stage Dockerfile (Maven build → JRE Alpine runtime).

#### API Documentation
- Interactive Swagger UI at `/swagger-ui.html`, restricted to `ROLE_ADMIN` and `ROLE_SUPER_ADMIN`.

#### Testing
- Unit test suite covering domain models, value objects, use cases, validators, services, and rate-limiting components.
- Integration test suite using Testcontainers (MySQL + RabbitMQ) covering login flows, account lock/unlock, logout, user creation, and password update.
- JaCoCo code coverage report (`mvn verify` → `target/site/jacoco/index.html`).

[1.0.0]: https://github.com/MiguelA-Izquierdo/user-service/releases/tag/v1.0.0