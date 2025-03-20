package units.userService.auth.domain.event;

import com.app.userService.auth.domain.event.UserLogoutDomainEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;


class UserLogoutDomainEventTest {

  @Test
  void testUserLogoutDomainEventProperties() {
    String userExchange = "user.exchange";
    String userLogoutQueue = "user.queue";
    String userLogoutRoutingKey = "user.logout.routing";
    UUID userId = UUID.randomUUID();
    String name = "John";
    String lastName = "Doe";
    String email = "john.doe@example.com";

    UserLogoutDomainEvent event = new UserLogoutDomainEvent(
      userExchange, userLogoutQueue, userLogoutRoutingKey, userId, name, lastName, email);

    Assertions.assertEquals(userExchange, event.getExchange());
    Assertions.assertEquals(userLogoutQueue, event.getQueue());
    Assertions.assertEquals(userLogoutRoutingKey, event.getRoutingKey());
    Assertions.assertEquals("user.logout", event.getType());

    UserLogoutDomainEvent.UserPayload payload = event.getPayload();
    Assertions.assertNotNull(payload);
    Assertions.assertEquals(userId, payload.userId());
    Assertions.assertEquals(name, payload.name());
    Assertions.assertEquals(lastName, payload.lastName());
    Assertions.assertEquals(email, payload.email());
  }

  @Test
  void testUserLogoutDomainEventPayload() {
    UUID userId = UUID.randomUUID();
    String name = "John";
    String lastName = "Doe";
    String email = "john.doe@example.com";

    UserLogoutDomainEvent.UserPayload payload = new UserLogoutDomainEvent.UserPayload(userId, name, lastName, email);

    Assertions.assertEquals(userId, payload.userId());
    Assertions.assertEquals(name, payload.name());
    Assertions.assertEquals(lastName, payload.lastName());
    Assertions.assertEquals(email, payload.email());
  }
}
