package com.app.userService.user.domain.model;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OutboxEventTest {

  private UUID id;
  private String eventType;
  private String payload;
  private OutboxEventStatus status;
  private LocalDateTime createdAt;
  private String queue;
  private String exchange;
  private String routingKey;

  @BeforeEach
  void setUp() {
    id = UUID.randomUUID();
    eventType = "UserCreated";
    payload = "{\"userId\": \"1234\", \"name\": \"John Doe\"}";
    status = OutboxEventStatus.PENDING;
    createdAt = LocalDateTime.now();
    queue = "userQueue";
    exchange = "userExchange";
    routingKey = "userRoutingKey";
  }

  @Test
  void testOutboxEventCreation() {
    OutboxEvent event = OutboxEvent.of(id, eventType, payload, status, createdAt, queue, exchange, routingKey);

    assertNotNull(event);
    assertEquals(id, event.getId());
    assertEquals(eventType, event.getType());
    assertEquals(payload, event.getPayload());
    assertEquals(queue, event.getQueue());
    assertEquals(exchange, event.getExchange());
    assertEquals(routingKey, event.getRoutingKey());
    assertEquals(status, event.getStatus());
    assertEquals(createdAt, event.getCreatedAt());
  }

  @Test
  void testMarkAsProcessed() {
    OutboxEvent event = OutboxEvent.of(id, eventType, payload, status, createdAt, queue, exchange, routingKey);

    event.markAsProcessed();

    assertEquals(OutboxEventStatus.PROCESSED, event.getStatus());
  }

  @Test
  void testMarkAsFailed() {
    OutboxEvent event = OutboxEvent.of(id, eventType, payload, status, createdAt, queue, exchange, routingKey);

    event.markAsFailed();

    assertEquals(OutboxEventStatus.FAILED, event.getStatus());
  }
}
