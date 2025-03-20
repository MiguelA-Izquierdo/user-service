package units.userService.auth.domain.event;

import com.app.userService.auth.domain.event.UserLockedDomainEvent;
import com.app.userService.auth.domain.event.UserUnlockedDomainEvent;
import com.app.userService.auth.domain.model.PasswordResetToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;


class UserUnLockedDomainEventTest {

  @Test
  void testUserUnLockedDomainEventProperties() {
    String userExchange = "user.exchange";
    String userUnLockedQueue = "user.queue";
    String userUnLockedRoutingKey = "user.created.routing";
    UUID userId = UUID.randomUUID();
    String name = "John";
    String lastName = "Doe";
    String email = "john.doe@example.com";

    UserUnlockedDomainEvent event = new UserUnlockedDomainEvent(
      userExchange, userUnLockedQueue, userUnLockedRoutingKey, userId, name, lastName, email);

    Assertions.assertEquals(userExchange, event.getExchange());
    Assertions.assertEquals(userUnLockedQueue, event.getQueue());
    Assertions.assertEquals(userUnLockedRoutingKey, event.getRoutingKey());
    Assertions.assertEquals("user.unlocked", event.getType());

    UserUnlockedDomainEvent.UserPayload payload = event.getPayload();
    Assertions.assertNotNull(payload);
    Assertions.assertEquals(userId, payload.userId());
    Assertions.assertEquals(name, payload.name());
    Assertions.assertEquals(lastName, payload.lastName());
    Assertions.assertEquals(email, payload.email());
  }

  @Test
  void testUserUnLockedDomainEventPayload() {
    UUID userId = UUID.randomUUID();
    String name = "John";
    String lastName = "Doe";
    String email = "john.doe@example.com";

    UserUnlockedDomainEvent.UserPayload payload = new UserUnlockedDomainEvent.UserPayload(userId, name, lastName, email);

    Assertions.assertEquals(userId, payload.userId());
    Assertions.assertEquals(name, payload.name());
    Assertions.assertEquals(lastName, payload.lastName());
    Assertions.assertEquals(email, payload.email());
  }
}
