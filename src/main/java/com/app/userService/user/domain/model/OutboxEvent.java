package com.app.userService.user.domain.model;

import com.app.userService.user.domain.event.Event;

import java.time.LocalDateTime;
import java.util.UUID;

public class OutboxEvent implements Event {

  private final UUID id;
  private final String type;
  private final String payload;
  private OutboxEventStatus status;
  private final LocalDateTime createdAt;
  private final String queue;
  private final String routingKey;
  private final String exchange;

  private OutboxEvent(UUID id,
                      String type,
                      String payload,
                      OutboxEventStatus status,
                      LocalDateTime createdAt,
                      String queue,
                      String exchange,
                      String routingKey) {
    this.id = id;
    this.type = type;
    this.payload = payload;
    this.status = status;
    this.createdAt = createdAt;
    this.queue = queue;
    this.routingKey = routingKey;
    this.exchange = exchange;
  }

  public static OutboxEvent of(UUID id,
                               String eventType,
                               String payload,
                               OutboxEventStatus status,
                               LocalDateTime createdAt,
                               String queue,
                               String routingKey,
                               String exchange) {
    return new OutboxEvent(id, eventType, payload, status, createdAt, queue, routingKey, exchange);
  }

  public void markAsProcessed() {
    this.status = OutboxEventStatus.PROCESSED;
  }

  public void markAsFailed() {
    this.status = OutboxEventStatus.FAILED;
  }
  public UUID getId() {
    return id;
  }
  @Override
  public String getType() {
    return type;
  }
  @Override
  public String getPayload() {
    return payload;
  }

  public OutboxEventStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
  @Override
  public String getQueue() {
    return queue;
  }

  @Override
  public String getExchange() {
    return exchange;
  }
  @Override
  public String getRoutingKey() {
    return routingKey;
  }
}

