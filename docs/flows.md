# Key flows

## Account lock and password reset

Triggered automatically when a user accumulates **5 consecutive failed login attempts**.

### Step 1 — Account gets locked

On the 5th failed attempt, `LoginService` locks the account and calls `UserEventService.handleUserLockedEvent()`, which does two things atomically within the same transaction:

1. Creates a `PasswordResetToken` (valid for **1 hour**) and persists it.
2. Stores a `user.locked` event in the outbox. The outbox publisher forwards it to RabbitMQ.

The `user.locked` event payload includes the reset `token` and its `expirationDate`. An external consumer (e.g. a notification service) is expected to receive this event and send the token to the user via email or similar channel.

```
user.locked event payload
{
  "userId": "...",
  "name": "...",
  "lastName": "...",
  "email": "...",
  "token": "<reset-token>",
  "expirationDate": "2025-01-01T12:00:00"
}
```

> This service does not send the email itself. Delivery is the responsibility of whoever consumes `userLockedQueue`.

### Step 2 — User resets password and unlocks the account

The user calls:

```
POST /auth/unlock-reset-password
Content-Type: application/json

{
  "token": "<reset-token>",
  "newPassword": "<new-password>"
}
```

`UnlockResetPasswordUseCase` executes the following in order:

| Step | What happens |
|---|---|
| 1 | Looks up the token in the database. Returns `404` if not found. |
| 2 | Validates the token: must not be expired and must not have been used already. Returns `401` if invalid. |
| 3 | Marks the token as used (prevents replay). |
| 4 | Resets the user's password (bcrypt hash of `newPassword`). Rotates `secretKey`, invalidating any existing JWT. |
| 5 | Unlocks the account: status → `ACTIVE`, `failedLoginAttempts` → 0. |
| 6 | Logs `UserAction.UNLOCKED`. |

Returns `204 No Content` on success. No event is published.

### Step 3 — User logs in normally

Once unlocked the user can authenticate via `POST /auth` with the new password.

---

### Flow summary

```
5 failed logins
      │
      ▼
Account locked ──► PasswordResetToken saved (TTL 1h)
      │
      ▼
user.locked event ──► RabbitMQ ──► consumer sends email with token
      │
      ▼ (user clicks link / calls API)
POST /auth/unlock-reset-password { token, newPassword }
      │
      ▼
Token validated ──► password reset ──► account unlocked
      │
      ▼
POST /auth { email, newPassword } ──► JWT issued
```

---

## Login

### With credentials (POST /auth)

1. Looks up user by email. Returns `404` if not found.
2. Checks account is not locked. Returns `423` if locked.
3. Validates password. On failure: increments `failedLoginAttempts`; if it reaches 5, triggers the lock flow above.
4. Resets `failedLoginAttempts` to 0 on success.
5. Publishes `user.logged.without.token` event.
6. Returns a signed JWT.

### With existing token (GET /auth)

1. Extracts `userId` from the Bearer token in the `Authorization` header.
2. Looks up user, checks account is active.
3. Publishes `user.logged.with.token` event.
4. Returns a new signed JWT (token is rotated on every call).

---

## Logout

```
POST /auth/logout           — logs out the authenticated user
POST /auth/logout/{userId}  — logs out another user (admin only)
```

1. Looks up user by ID.
2. Rotates `secretKey`, which invalidates all existing JWTs for that user immediately.
3. Publishes `user.logout` event.
4. Returns `204 No Content`.

> JWT invalidation works because every token is validated against the user's current `secretKey`. Rotating it makes all previously issued tokens fail validation without needing a token blacklist.