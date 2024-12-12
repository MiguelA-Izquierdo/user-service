package com.app.userService.user.infrastructure.repository;

import com.app.userService.user.domain.model.OutboxEvent;
import com.app.userService.user.domain.model.OutboxEventStatus;
import com.app.userService.user.infrastructure.entities.OutboxEventEntity;


import java.util.List;
import java.util.UUID;

public interface OutboxEventJpaRepository extends org.springframework.data.jpa.repository.JpaRepository<OutboxEventEntity, UUID>{
  void save(OutboxEvent outboxEvent);
  List<OutboxEventEntity> findByStatus(OutboxEventStatus status);
  List<OutboxEventEntity> findByQueue(String queue);
  List<OutboxEventEntity> findByRoutingKey(String queue);
}
