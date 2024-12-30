package com.app.userService.user.infrastructure.messaging.outbound;

import com.app.userService.user.domain.model.OutboxEvent;
import com.app.userService.user.domain.model.OutboxEventStatus;
import com.app.userService.user.domain.repositories.OutboxEventRepository;
import com.app.userService.user.domain.service.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OutboxEventPublisher {
  private static final Logger logger = LoggerFactory.getLogger(OutboxEventPublisher.class);

  private final OutboxEventRepository outboxEventRepository;
  private final EventPublisher eventPublisher;

  public OutboxEventPublisher(OutboxEventRepository outboxEventRepository,
                              EventPublisher eventPublisher) {
    this.outboxEventRepository = outboxEventRepository;
    this.eventPublisher = eventPublisher;
  }

  @Scheduled(fixedRate = 5000)
  public void publishPendingEvents() {
    List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatus(OutboxEventStatus.PENDING);

    pendingEvents.forEach(event -> {
      try {
        logger.debug("Vamos a publicar el evento {} {}", event.getId(), event.getRoutingKey());
        eventPublisher.publish(event);
        event.markAsProcessed();
        outboxEventRepository.save(event);

      } catch (Exception e) {
        logger.error("Error al publicar el evento {}", e.getMessage());
        event.markAsFailed();
        outboxEventRepository.save(event);
      }
    });
  }
}
