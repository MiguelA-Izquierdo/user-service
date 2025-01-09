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
                               String routingKey) {
    return new Builder()
      .id(id)
      .type(eventType)
      .payload(payload)
      .status(status)
      .createdAt(createdAt)
      .queue(queue)
      .exchange(exchange)
      .routingKey(routingKey)
      .build();
  }

  public static Builder builder() {
    return new Builder();
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

  public static class Builder {
    private UUID id;
    private String type;
    private String payload;
    private OutboxEventStatus status;
    private LocalDateTime createdAt;
    private String queue;
    private String routingKey;
    private String exchange;

    public Builder id(UUID id) {
      this.id = id;
      return this;
    }

    public Builder type(String type) {
      this.type = type;
      return this;
    }

    public Builder payload(String payload) {
      this.payload = payload;
      return this;
    }

    public Builder status(OutboxEventStatus status) {
      this.status = status;
      return this;
    }

    public Builder createdAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder queue(String queue) {
      this.queue = queue;
      return this;
    }

    public Builder routingKey(String routingKey) {
      this.routingKey = routingKey;
      return this;
    }

    public Builder exchange(String exchange) {
      this.exchange = exchange;
      return this;
    }

    public OutboxEvent build() {
      return new OutboxEvent(id, type, payload, status, createdAt, queue, exchange, routingKey);
    }
  }
}
