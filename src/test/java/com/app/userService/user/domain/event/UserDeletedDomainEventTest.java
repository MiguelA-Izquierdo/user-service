package com.app.userService.user.domain.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

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

    assertEquals(userExchange, event.getExchange());
    assertEquals(userDeletedQueue, event.getQueue());
    assertEquals(userDeletedRoutingKey, event.getRoutingKey());
    assertEquals("user.deleted", event.getType());

    UserDeletedDomainEvent.UserPayload payload = event.getPayload();
    assertNotNull(payload);
    assertEquals(userId, payload.userId());
    assertEquals(name, payload.name());
    assertEquals(lastName, payload.lastName());
    assertEquals(email, payload.email());
  }

  @Test
  void testUserDeletedDomainEventPayload() {
    UUID userId = UUID.randomUUID();
    String name = "Jane";
    String lastName = "Smith";
    String email = "jane.smith@example.com";

    UserDeletedDomainEvent.UserPayload payload = new UserDeletedDomainEvent.UserPayload(userId, name, lastName, email);

    assertEquals(userId, payload.userId());
    assertEquals(name, payload.name());
    assertEquals(lastName, payload.lastName());
    assertEquals(email, payload.email());
  }
}
