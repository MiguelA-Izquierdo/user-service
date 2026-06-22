package com.app.userService.user.infrastructure.repositories;

import com.app.userService.user.domain.model.OutboxEvent;
import com.app.userService.user.domain.model.OutboxEventStatus;
import com.app.userService.user.infrastructure.entities.OutboxEventEntity;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.QueryHint;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface OutboxEventJpaRepository extends JpaRepository<OutboxEventEntity, UUID>{
  void save(OutboxEvent outboxEvent);
  List<OutboxEventEntity> findByStatus(OutboxEventStatus status);
  List<OutboxEventEntity> findByQueue(String queue);
  List<OutboxEventEntity> findByRoutingKey(String queue);

  /**
   * Claims publishable rows with a pessimistic write lock. The {@code jakarta.persistence.lock.timeout}
   * hint of -2 maps to Hibernate's LockOptions.SKIP_LOCKED, so the generated SQL is
   * SELECT ... FOR UPDATE SKIP LOCKED — rows already locked by another instance are skipped
   * instead of blocking. Requires MySQL 8+ / a SKIP LOCKED-capable engine.
   */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "-2"))
  @Query("SELECT e FROM OutboxEventEntity e WHERE e.status IN :statuses " +
         "AND (e.nextRetryAt IS NULL OR e.nextRetryAt <= :now) ORDER BY e.createdAt")
  List<OutboxEventEntity> fetchPublishableBatch(@Param("statuses") Collection<OutboxEventStatus> statuses,
                                                @Param("now") LocalDateTime now,
                                                Pageable pageable);
}
