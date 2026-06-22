package com.app.userService.user.infrastructure.messaging.outbound;

import com.app.userService.user.domain.model.OutboxEvent;
import com.app.userService.user.domain.repositories.OutboxEventRepository;
import com.app.userService.user.domain.service.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OutboxEventPublisher {
  private static final Logger logger = LoggerFactory.getLogger(OutboxEventPublisher.class);

  private final OutboxEventRepository outboxEventRepository;
  private final EventPublisher eventPublisher;
  private final int batchSize;
  private final int maxAttempts;
  private final Duration baseBackoff;

  public OutboxEventPublisher(OutboxEventRepository outboxEventRepository,
                              EventPublisher eventPublisher,
                              @Value("${outbox.publisher.batch-size:100}") int batchSize,
                              @Value("${outbox.publisher.max-attempts:5}") int maxAttempts,
                              @Value("${outbox.publisher.backoff-seconds:10}") long backoffSeconds) {
    this.outboxEventRepository = outboxEventRepository;
    this.eventPublisher = eventPublisher;
    this.batchSize = batchSize;
    this.maxAttempts = maxAttempts;
    this.baseBackoff = Duration.ofSeconds(backoffSeconds);
  }

  /**
   * Claims a batch of publishable events with row-level locks (SKIP LOCKED) and publishes them.
   * Running inside a single transaction keeps the locks held until commit, so a second instance
   * polling concurrently skips these rows instead of double-publishing them. A transient publish
   * failure schedules a retry with exponential backoff; once the attempt budget is exhausted the
   * event is parked as DEAD rather than being retried forever.
   */
  @Scheduled(fixedRateString = "${outbox.publisher.poll-interval-ms:5000}")
  @Transactional
  public void publishPendingEvents() {
    List<OutboxEvent> batch = outboxEventRepository.fetchPublishableBatch(batchSize, LocalDateTime.now());
    if (batch.isEmpty()) {
      return;
    }

    for (OutboxEvent event : batch) {
      try {
        logger.debug("Publishing outbox event {} queue={} routingKey={}", event.getId(), event.getQueue(), event.getRoutingKey());
        eventPublisher.publish(event);
        event.markAsProcessed();
      } catch (Exception e) {
        event.registerFailure(maxAttempts, baseBackoff);
        if (event.isDead()) {
          logger.error("Outbox event {} exhausted {} attempts; parked as DEAD: {}", event.getId(), maxAttempts, e.getMessage());
        } else {
          logger.warn("Failed to publish outbox event {} (attempt {}/{}), retry at {}: {}",
            event.getId(), event.getAttempts(), maxAttempts, event.getNextRetryAt(), e.getMessage());
        }
      }
      outboxEventRepository.save(event);
    }
  }
}