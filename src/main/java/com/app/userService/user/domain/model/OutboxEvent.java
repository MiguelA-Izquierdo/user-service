package com.app.userService.user.domain.model;

import com.app.userService.user.domain.event.Event;

import java.time.LocalDateTime;
import java.util.UUID;

public class OutboxEvent implements Event<String> {

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
                               String exchange,
                               String routingKey
                              ) {
    return new OutboxEvent(id, eventType, payload, status, createdAt, queue, routingKey, exchange);
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
  public OutboxEventStatus getStatus() {
    return status;
  }
  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
  public void markAsProcessed() {
    this.status = OutboxEventStatus.PROCESSED;
  }

  public void markAsFailed() {
    this.status = OutboxEventStatus.FAILED;
  }
}

