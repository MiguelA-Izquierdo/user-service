package com.app.userService.user.domain.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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

    assertEquals(userExchange, event.getExchange());
    assertEquals(userCreatedQueue, event.getQueue());
    assertEquals(userCreatedRoutingKey, event.getRoutingKey());
    assertEquals("user.created", event.getType());

    UserCreatedDomainEvent.UserPayload payload = event.getPayload();
    assertNotNull(payload);
    assertEquals(userId, payload.userId());
    assertEquals(name, payload.name());
    assertEquals(lastName, payload.lastName());
    assertEquals(email, payload.email());
  }

  @Test
  void testUserCreatedDomainEventPayload() {
    UUID userId = UUID.randomUUID();
    String name = "Jane";
    String lastName = "Doe";
    String email = "jane.doe@example.com";

    UserCreatedDomainEvent.UserPayload payload = new UserCreatedDomainEvent.UserPayload(userId, name, lastName, email);

    assertEquals(userId, payload.userId());
    assertEquals(name, payload.name());
    assertEquals(lastName, payload.lastName());
    assertEquals(email, payload.email());
  }
}
