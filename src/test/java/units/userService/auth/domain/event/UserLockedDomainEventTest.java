package units.userService.auth.domain.event;

import com.app.userService.auth.domain.event.UserLockedDomainEvent;
import com.app.userService.auth.domain.model.PasswordResetToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;


class UserLockedDomainEventTest {

  @Test
  void testUserLockedDomainEventProperties() {
    String userExchange = "user.exchange";
    String userLockedQueue = "user.queue";
    String userLockedRoutingKey = "user.created.routing";
    UUID userId = UUID.randomUUID();
    String name = "John";
    String lastName = "Doe";
    String email = "john.doe@example.com";
    String token = "test-token";
    LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(PasswordResetToken.TOKEN_EXPIRATION_TIME_SECONDS);

    UserLockedDomainEvent event = new UserLockedDomainEvent(
      userExchange, userLockedQueue, userLockedRoutingKey, userId, name, lastName, email, token, expirationDate);

    Assertions.assertEquals(userExchange, event.getExchange());
    Assertions.assertEquals(userLockedQueue, event.getQueue());
    Assertions.assertEquals(userLockedRoutingKey, event.getRoutingKey());
    Assertions.assertEquals("user.locked", event.getType());

    UserLockedDomainEvent.UserPayload payload = event.getPayload();
    Assertions.assertNotNull(payload);
    Assertions.assertEquals(userId, payload.userId());
    Assertions.assertEquals(name, payload.name());
    Assertions.assertEquals(lastName, payload.lastName());
    Assertions.assertEquals(email, payload.email());
  }

  @Test
  void testUserLockedDomainEventPayload() {
    UUID userId = UUID.randomUUID();
    String name = "John";
    String lastName = "Doe";
    String email = "john.doe@example.com";
    String token = "test-token";
    LocalDateTime expirationDate = LocalDateTime.now().plusSeconds(PasswordResetToken.TOKEN_EXPIRATION_TIME_SECONDS);

    UserLockedDomainEvent.UserPayload payload = new UserLockedDomainEvent.UserPayload(userId, name, lastName, email, token, expirationDate);

    Assertions.assertEquals(userId, payload.userId());
    Assertions.assertEquals(name, payload.name());
    Assertions.assertEquals(lastName, payload.lastName());
    Assertions.assertEquals(email, payload.email());
  }
}
