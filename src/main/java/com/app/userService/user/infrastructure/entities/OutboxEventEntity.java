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

  public OutboxEventEntity(){}
  public OutboxEventEntity(UUID id, String eventType, String payload, OutboxEventStatus status,
                           LocalDateTime createdAt, String queue, String routingKey, String exchange) {
    this.id = id;
    this.eventType = eventType;
    this.payload = payload;
    this.status = status;
    this.createdAt = createdAt;
    this.queue = queue;
    this.routingKey = routingKey;
    this.exchange = exchange;
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
}
