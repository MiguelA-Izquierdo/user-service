package com.app.userService.user.infrastructure.mapper;

import com.app.userService.user.domain.model.OutboxEvent;
import com.app.userService.user.infrastructure.entities.OutboxEventEntity;

public class OutboxEventMapper {

  public static OutboxEventEntity toEntity(OutboxEvent event) {
    return new OutboxEventEntity(
      event.getId(),
      event.getType(),
      event.getPayload(),
      event.getStatus(),
      event.getCreatedAt(),
      event.getQueue(),
      event.getRoutingKey(),
      event.getExchange()
    );
  }

  public static OutboxEvent toDomain(OutboxEventEntity entity) {
    return  OutboxEvent.of(
      entity.getId(),
      entity.getEventType(),
      entity.getPayload(),
      entity.getStatus(),
      entity.getCreatedAt(),
      entity.getQueue(),
      entity.getRoutingKey(),
      entity.getExchange()
    );
  }
}
