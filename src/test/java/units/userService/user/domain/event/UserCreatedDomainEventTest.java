package units.userService.user.domain.event;

import com.app.userService.user.domain.event.UserCreatedDomainEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;


class UserCreatedDomainEventTest {

  @Test
  void testUserCreatedDomainEventProperties() {
    String userExchange = "user.exchange";
    String userCreatedQueue = "user.queue";
    String userCreatedRoutingKey = "user.created.routing";
    UUID userId = UUID.randomUUID();
    String name = "John";
    String lastName = "Doe";
    String email = "john.doe@example.com";

    UserCreatedDomainEvent event = new UserCreatedDomainEvent(
      userExchange, userCreatedQueue, userCreatedRoutingKey, userId, name, lastName, email);

    Assertions.assertEquals(userExchange, event.getExchange());
    Assertions.assertEquals(userCreatedQueue, event.getQueue());
    Assertions.assertEquals(userCreatedRoutingKey, event.getRoutingKey());
    Assertions.assertEquals("user.created", event.getType());

    UserCreatedDomainEvent.UserPayload payload = event.getPayload();
    Assertions.assertNotNull(payload);
    Assertions.assertEquals(userId, payload.userId());
    Assertions.assertEquals(name, payload.name());
    Assertions.assertEquals(lastName, payload.lastName());
    Assertions.assertEquals(email, payload.email());
  }

  @Test
  void testUserCreatedDomainEventPayload() {
    UUID userId = UUID.randomUUID();
    String name = "Jane";
    String lastName = "Doe";
    String email = "jane.doe@example.com";

    UserCreatedDomainEvent.UserPayload payload = new UserCreatedDomainEvent.UserPayload(userId, name, lastName, email);

    Assertions.assertEquals(userId, payload.userId());
    Assertions.assertEquals(name, payload.name());
    Assertions.assertEquals(lastName, payload.lastName());
    Assertions.assertEquals(email, payload.email());
  }
}
