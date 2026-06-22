package com.app.userService.user.domain.model;

import com.app.userService._shared.domain.event.Event;

import java.time.Duration;
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
  private int attempts;
  private LocalDateTime nextRetryAt;

  private OutboxEvent(UUID id,
                      String type,
                      String payload,
                      OutboxEventStatus status,
                      LocalDateTime createdAt,
                      String queue,
                      String exchange,
                      String routingKey,
                      int attempts,
                      LocalDateTime nextRetryAt) {
    this.id = id;
    this.type = type;
    this.payload = payload;
    this.status = status;
    this.createdAt = createdAt;
    this.queue = queue;
    this.routingKey = routingKey;
    this.exchange = exchange;
    this.attempts = attempts;
    this.nextRetryAt = nextRetryAt;
  }

  public static Builder builder() {
    return new Builder();
  }

  public UUID getId() {
    return id;
  }

  @Override
  public UUID getEventId() {
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

  public int getAttempts() {
    return attempts;
  }

  public LocalDateTime getNextRetryAt() {
    return nextRetryAt;
  }

  public boolean isDead() {
    return this.status == OutboxEventStatus.DEAD;
  }

  public void markAsProcessed() {
    this.status = OutboxEventStatus.PROCESSED;
    this.nextRetryAt = null;
  }

  /**
   * Records a failed publish attempt. Increments the attempt counter and either schedules a retry
   * with exponential backoff (status FAILED, nextRetryAt set in the future) or, once maxAttempts is
   * reached, parks the event as DEAD so the poller stops picking it up. A FAILED event is
   * republishable; a DEAD one is terminal and requires manual intervention.
   */
  public void registerFailure(int maxAttempts, Duration baseBackoff) {
    this.attempts += 1;
    if (this.attempts >= maxAttempts) {
      this.status = OutboxEventStatus.DEAD;
      this.nextRetryAt = null;
    } else {
      this.status = OutboxEventStatus.FAILED;
      this.nextRetryAt = LocalDateTime.now().plus(backoffFor(this.attempts, baseBackoff));
    }
  }

  // Exponential backoff: base * 2^(attempt-1), e.g. for a 10s base -> 10s, 20s, 40s, 80s...
  private static Duration backoffFor(int attempt, Duration baseBackoff) {
    return baseBackoff.multipliedBy(1L << (attempt - 1));
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
    private int attempts;
    private LocalDateTime nextRetryAt;

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

    public Builder attempts(int attempts) {
      this.attempts = attempts;
      return this;
    }

    public Builder nextRetryAt(LocalDateTime nextRetryAt) {
      this.nextRetryAt = nextRetryAt;
      return this;
    }

    public OutboxEvent build() {
      return new OutboxEvent(id, type, payload, status, createdAt, queue, exchange, routingKey, attempts, nextRetryAt);
    }
  }
}
