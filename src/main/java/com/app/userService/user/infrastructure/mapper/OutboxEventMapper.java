package com.app.userService.user.infrastructure.mapper;

import com.app.userService.user.domain.model.OutboxEvent;
import com.app.userService.user.infrastructure.entities.OutboxEventEntity;

public class OutboxEventMapper {

  public static OutboxEventEntity toEntity(OutboxEvent event) {
    return OutboxEventEntity.builder()
      .id(event.getId())
      .eventType(event.getType())
      .payload(event.getPayload())
      .status(event.getStatus())
      .createdAt(event.getCreatedAt())
      .queue(event.getQueue())
      .routingKey(event.getRoutingKey())
      .exchange(event.getExchange())
      .attempts(event.getAttempts())
      .nextRetryAt(event.getNextRetryAt())
      .build();
  }

  public static OutboxEvent toDomain(OutboxEventEntity entity) {
    return OutboxEvent.builder()
      .id(entity.getId())
      .type(entity.getEventType())
      .payload(entity.getPayload())
      .status(entity.getStatus())
      .createdAt(entity.getCreatedAt())
      .queue(entity.getQueue())
      .routingKey(entity.getRoutingKey())
      .exchange(entity.getExchange())
      .attempts(entity.getAttempts())
      .nextRetryAt(entity.getNextRetryAt())
      .build();
  }
}
