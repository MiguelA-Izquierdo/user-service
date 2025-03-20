package units.userService.auth.domain.event;

import com.app.userService.auth.domain.event.UserLoggedDomainEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;


class UserLoggedDomainEventTest {

  @Test
  void testUserLoggedDomainEventProperties() {
    String userExchange = "user.exchange";
    String userLoggedQueue = "user.queue";
    String userLoggedRoutingKey = "user.logged.routing";
    UUID userId = UUID.randomUUID();
    String name = "John";
    String lastName = "Doe";
    String email = "john.doe@example.com";

    UserLoggedDomainEvent event = new UserLoggedDomainEvent(
      userExchange, userLoggedQueue, userLoggedRoutingKey, userId, name, lastName, email);

    Assertions.assertEquals(userExchange, event.getExchange());
    Assertions.assertEquals(userLoggedQueue, event.getQueue());
    Assertions.assertEquals(userLoggedRoutingKey, event.getRoutingKey());
    Assertions.assertEquals("user.logged", event.getType());

    UserLoggedDomainEvent.UserPayload payload = event.getPayload();
    Assertions.assertNotNull(payload);
    Assertions.assertEquals(userId, payload.userId());
    Assertions.assertEquals(name, payload.name());
    Assertions.assertEquals(lastName, payload.lastName());
    Assertions.assertEquals(email, payload.email());
  }

  @Test
  void testUserLoggedDomainEventPayload() {
    UUID userId = UUID.randomUUID();
    String name = "John";
    String lastName = "Doe";
    String email = "john.doe@example.com";

    UserLoggedDomainEvent.UserPayload payload = new UserLoggedDomainEvent.UserPayload(userId, name, lastName, email);

    Assertions.assertEquals(userId, payload.userId());
    Assertions.assertEquals(name, payload.name());
    Assertions.assertEquals(lastName, payload.lastName());
    Assertions.assertEquals(email, payload.email());
  }
}
