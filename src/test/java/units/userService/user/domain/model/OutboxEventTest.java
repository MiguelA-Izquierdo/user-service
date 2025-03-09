package units.userService.user.domain.model;


import com.app.userService.user.domain.model.OutboxEvent;
import com.app.userService.user.domain.model.OutboxEventStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;


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

    Assertions.assertNotNull(event);
    Assertions.assertEquals(id, event.getId());
    Assertions.assertEquals(eventType, event.getType());
    Assertions.assertEquals(payload, event.getPayload());
    Assertions.assertEquals(queue, event.getQueue());
    Assertions.assertEquals(exchange, event.getExchange());
    Assertions.assertEquals(routingKey, event.getRoutingKey());
    Assertions.assertEquals(status, event.getStatus());
    Assertions.assertEquals(createdAt, event.getCreatedAt());
  }

  @Test
  void testMarkAsProcessed() {
    OutboxEvent event = OutboxEvent.of(id, eventType, payload, status, createdAt, queue, exchange, routingKey);

    event.markAsProcessed();

    Assertions.assertEquals(OutboxEventStatus.PROCESSED, event.getStatus());
  }

  @Test
  void testMarkAsFailed() {
    OutboxEvent event = OutboxEvent.of(id, eventType, payload, status, createdAt, queue, exchange, routingKey);

    event.markAsFailed();

    Assertions.assertEquals(OutboxEventStatus.FAILED, event.getStatus());
  }
}
