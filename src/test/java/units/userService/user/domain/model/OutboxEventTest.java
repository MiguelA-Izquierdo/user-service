package units.userService.user.domain.model;


import com.app.userService.user.domain.model.OutboxEvent;
import com.app.userService.user.domain.model.OutboxEventStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
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
    OutboxEvent event = OutboxEvent.builder()
      .id(id).type(eventType).payload(payload).status(status)
      .createdAt(createdAt).queue(queue).exchange(exchange).routingKey(routingKey)
      .build();

    Assertions.assertNotNull(event);
    Assertions.assertEquals(id, event.getId());
    Assertions.assertEquals(id, event.getEventId());
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
    OutboxEvent event = newEvent();

    event.markAsProcessed();

    Assertions.assertEquals(OutboxEventStatus.PROCESSED, event.getStatus());
    Assertions.assertNull(event.getNextRetryAt());
  }

  @Test
  void registerFailure_belowMaxAttempts_schedulesRetryWithFutureBackoff() {
    OutboxEvent event = newEvent();

    event.registerFailure(3, Duration.ofSeconds(10));

    Assertions.assertEquals(OutboxEventStatus.FAILED, event.getStatus());
    Assertions.assertEquals(1, event.getAttempts());
    Assertions.assertNotNull(event.getNextRetryAt());
    Assertions.assertTrue(event.getNextRetryAt().isAfter(LocalDateTime.now()),
      "nextRetryAt must be scheduled in the future");
    Assertions.assertFalse(event.isDead());
  }

  @Test
  void registerFailure_backoffIsExponential() {
    OutboxEvent first = newEvent();
    OutboxEvent second = newEvent();

    LocalDateTime before = LocalDateTime.now();
    first.registerFailure(5, Duration.ofSeconds(10));   // attempt 1 -> ~10s
    second.registerFailure(5, Duration.ofSeconds(10));
    second.registerFailure(5, Duration.ofSeconds(10));  // attempt 2 -> ~20s

    Assertions.assertTrue(second.getNextRetryAt().isAfter(first.getNextRetryAt()),
      "later attempts must back off further than earlier ones");
    Assertions.assertTrue(first.getNextRetryAt().isAfter(before.plusSeconds(5)));
  }

  @Test
  void registerFailure_reachingMaxAttempts_marksDeadAndStopsRetrying() {
    OutboxEvent event = newEvent();
    int maxAttempts = 3;

    event.registerFailure(maxAttempts, Duration.ofSeconds(10)); // 1 -> FAILED
    event.registerFailure(maxAttempts, Duration.ofSeconds(10)); // 2 -> FAILED
    event.registerFailure(maxAttempts, Duration.ofSeconds(10)); // 3 -> DEAD

    Assertions.assertEquals(OutboxEventStatus.DEAD, event.getStatus());
    Assertions.assertEquals(3, event.getAttempts());
    Assertions.assertNull(event.getNextRetryAt(), "a DEAD event must not be scheduled for retry");
    Assertions.assertTrue(event.isDead());
  }

  private OutboxEvent newEvent() {
    return OutboxEvent.builder()
      .id(id).type(eventType).payload(payload).status(status)
      .createdAt(createdAt).queue(queue).exchange(exchange).routingKey(routingKey)
      .build();
  }
}
