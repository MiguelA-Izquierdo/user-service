package units.userService.user.infrastructure.messaging.outbound;

import com.app.userService.user.domain.model.OutboxEvent;
import com.app.userService.user.domain.model.OutboxEventStatus;
import com.app.userService.user.domain.repositories.OutboxEventRepository;
import com.app.userService.user.domain.service.EventPublisher;
import com.app.userService.user.infrastructure.messaging.outbound.OutboxEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class OutboxEventPublisherTest {

  private static final int MAX_ATTEMPTS = 3;
  private static final long BACKOFF_SECONDS = 10;

  @Mock private OutboxEventRepository outboxEventRepository;
  @Mock private EventPublisher eventPublisher;
  @Captor private ArgumentCaptor<OutboxEvent> savedEvent;

  private OutboxEventPublisher publisher;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    publisher = new OutboxEventPublisher(outboxEventRepository, eventPublisher, 100, MAX_ATTEMPTS, BACKOFF_SECONDS);
  }

  @Test
  void successfulPublish_marksEventProcessed() {
    OutboxEvent event = pendingEvent();
    when(outboxEventRepository.fetchPublishableBatch(anyInt(), any())).thenReturn(List.of(event));

    publisher.publishPendingEvents();

    verify(eventPublisher).publish(event);
    verify(outboxEventRepository).save(savedEvent.capture());
    assertEquals(OutboxEventStatus.PROCESSED, savedEvent.getValue().getStatus());
  }

  @Test
  void transientFailure_schedulesRetryWithoutDuplicatingDelivery() {
    OutboxEvent event = pendingEvent();
    when(outboxEventRepository.fetchPublishableBatch(anyInt(), any())).thenReturn(List.of(event));
    doThrow(new RuntimeException("RabbitMQ unreachable")).when(eventPublisher).publish(any());

    publisher.publishPendingEvents();

    verify(outboxEventRepository).save(event);
    assertEquals(OutboxEventStatus.FAILED, event.getStatus());
    assertEquals(1, event.getAttempts());
    assertNotNull(event.getNextRetryAt());
    assertFalse(event.isDead());
  }

  @Test
  void failureAtLastAttempt_parksEventAsDead() {
    // Event already failed MAX_ATTEMPTS - 1 times; the next failure exhausts the budget.
    OutboxEvent event = pendingEvent();
    for (int i = 0; i < MAX_ATTEMPTS - 1; i++) {
      event.registerFailure(MAX_ATTEMPTS, java.time.Duration.ofSeconds(BACKOFF_SECONDS));
    }
    when(outboxEventRepository.fetchPublishableBatch(anyInt(), any())).thenReturn(List.of(event));
    doThrow(new RuntimeException("RabbitMQ still unreachable")).when(eventPublisher).publish(any());

    publisher.publishPendingEvents();

    assertEquals(OutboxEventStatus.DEAD, event.getStatus());
    assertNull(event.getNextRetryAt());
    verify(outboxEventRepository).save(event);
  }

  @Test
  void emptyBatch_doesNothing() {
    when(outboxEventRepository.fetchPublishableBatch(anyInt(), any())).thenReturn(List.of());

    publisher.publishPendingEvents();

    verifyNoInteractions(eventPublisher);
    verify(outboxEventRepository, never()).save(any());
  }

  @Test
  void oneFailureDoesNotAbortTheRestOfTheBatch() {
    OutboxEvent failing = pendingEvent();
    OutboxEvent succeeding = pendingEvent();
    when(outboxEventRepository.fetchPublishableBatch(anyInt(), any())).thenReturn(List.of(failing, succeeding));
    doThrow(new RuntimeException("boom")).when(eventPublisher).publish(failing);

    publisher.publishPendingEvents();

    assertEquals(OutboxEventStatus.FAILED, failing.getStatus());
    assertEquals(OutboxEventStatus.PROCESSED, succeeding.getStatus());
    verify(outboxEventRepository, times(2)).save(any());
  }

  private OutboxEvent pendingEvent() {
    return OutboxEvent.builder()
      .id(UUID.randomUUID())
      .type("UserCreated")
      .payload("{}")
      .status(OutboxEventStatus.PENDING)
      .createdAt(LocalDateTime.now())
      .queue("userCreatedQueue")
      .exchange("userExchange")
      .routingKey("user.created")
      .build();
  }
}