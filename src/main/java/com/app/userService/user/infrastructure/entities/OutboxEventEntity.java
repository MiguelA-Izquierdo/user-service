package com.app.userService.user.infrastructure.entities;

import com.app.userService.user.domain.model.OutboxEventStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEventEntity {

  @Id
  @Column(name = "id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
  private UUID id;
  @Column(nullable = false)
  private String eventType;
  @Column(nullable = false)
  private String payload;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OutboxEventStatus status;
  @Column(nullable = false)
  private LocalDateTime createdAt;
  @Column(nullable = false)
  private String queue;
  @Column(nullable = false)
  private String routingKey;
  @Column(nullable = false)
  private String exchange;
  @Column(nullable = false)
  private int attempts;
  @Column
  private LocalDateTime nextRetryAt;

  public OutboxEventEntity() {}

  private OutboxEventEntity(Builder builder) {
    this.id = builder.id;
    this.eventType = builder.eventType;
    this.payload = builder.payload;
    this.status = builder.status;
    this.createdAt = builder.createdAt;
    this.queue = builder.queue;
    this.routingKey = builder.routingKey;
    this.exchange = builder.exchange;
    this.attempts = builder.attempts;
    this.nextRetryAt = builder.nextRetryAt;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UUID id;
    private String eventType;
    private String payload;
    private OutboxEventStatus status;
    private LocalDateTime createdAt;
    private String queue;
    private String routingKey;
    private String exchange;
    private int attempts;
    private LocalDateTime nextRetryAt;

    public Builder id(UUID id) { this.id = id; return this; }
    public Builder eventType(String eventType) { this.eventType = eventType; return this; }
    public Builder payload(String payload) { this.payload = payload; return this; }
    public Builder status(OutboxEventStatus status) { this.status = status; return this; }
    public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
    public Builder queue(String queue) { this.queue = queue; return this; }
    public Builder routingKey(String routingKey) { this.routingKey = routingKey; return this; }
    public Builder exchange(String exchange) { this.exchange = exchange; return this; }
    public Builder attempts(int attempts) { this.attempts = attempts; return this; }
    public Builder nextRetryAt(LocalDateTime nextRetryAt) { this.nextRetryAt = nextRetryAt; return this; }

    public OutboxEventEntity build() { return new OutboxEventEntity(this); }
  }


  public UUID getId() {
    return id;
  }

  public String getEventType() {
    return eventType;
  }

  public String getPayload() {
    return payload;
  }

  public OutboxEventStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public String getQueue() {
    return queue;
  }

  public String getRoutingKey() {
    return routingKey;
  }

  public String getExchange() {
    return exchange;
  }

  public int getAttempts() {
    return attempts;
  }

  public LocalDateTime getNextRetryAt() {
    return nextRetryAt;
  }
}
