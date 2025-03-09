package units.userService.user.domain.event;

import com.app.userService.user.domain.event.UserDeletedDomainEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;


class UserDeletedDomainEventTest {

  @Test
  void testUserDeletedDomainEventProperties() {
    String userExchange = "user.exchange";
    String userDeletedQueue = "user.deleted.queue";
    String userDeletedRoutingKey = "user.deleted.routing";
    UUID userId = UUID.randomUUID();
    String name = "John";
    String lastName = "Doe";
    String email = "john.doe@example.com";

    UserDeletedDomainEvent event = new UserDeletedDomainEvent(
      userExchange, userDeletedQueue, userDeletedRoutingKey, userId, name, lastName, email);

    Assertions.assertEquals(userExchange, event.getExchange());
    Assertions.assertEquals(userDeletedQueue, event.getQueue());
    Assertions.assertEquals(userDeletedRoutingKey, event.getRoutingKey());
    Assertions.assertEquals("user.deleted", event.getType());

    UserDeletedDomainEvent.UserPayload payload = event.getPayload();
    Assertions.assertNotNull(payload);
    Assertions.assertEquals(userId, payload.userId());
    Assertions.assertEquals(name, payload.name());
    Assertions.assertEquals(lastName, payload.lastName());
    Assertions.assertEquals(email, payload.email());
  }

  @Test
  void testUserDeletedDomainEventPayload() {
    UUID userId = UUID.randomUUID();
    String name = "Jane";
    String lastName = "Smith";
    String email = "jane.smith@example.com";

    UserDeletedDomainEvent.UserPayload payload = new UserDeletedDomainEvent.UserPayload(userId, name, lastName, email);

    Assertions.assertEquals(userId, payload.userId());
    Assertions.assertEquals(name, payload.name());
    Assertions.assertEquals(lastName, payload.lastName());
    Assertions.assertEquals(email, payload.email());
  }
}
