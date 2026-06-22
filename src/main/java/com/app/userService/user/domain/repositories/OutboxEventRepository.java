package com.app.userService.user.domain.repositories;

import com.app.userService.user.domain.model.*;

import java.time.LocalDateTime;
import java.util.List;

public interface OutboxEventRepository {
  void save(OutboxEvent outboxEvent);
  List<OutboxEvent> findByStatus(OutboxEventStatus status);
  List<OutboxEvent> findByQueue(String queue);
  List<OutboxEvent> findByRoutingKey(String routingKey);

  /**
   * Atomically claims up to {@code batchSize} publishable events (PENDING, or FAILED whose
   * retry time has elapsed) using row-level locks with SKIP LOCKED, so concurrent publisher
   * instances never select the same row. Must be called within a transaction; the locks are
   * held until it commits.
   */
  List<OutboxEvent> fetchPublishableBatch(int batchSize, LocalDateTime now);
}
