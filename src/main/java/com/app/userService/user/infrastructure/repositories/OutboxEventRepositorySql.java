package com.app.userService.user.infrastructure.repositories;

import com.app.userService.user.domain.model.*;
import com.app.userService.user.domain.repositories.OutboxEventRepository;
import com.app.userService.user.infrastructure.entities.OutboxEventEntity;
import com.app.userService.user.infrastructure.mapper.OutboxEventMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Repository;

import java.util.List;



@Repository
public class OutboxEventRepositorySql implements OutboxEventRepository {
  private static final Logger logger = LoggerFactory.getLogger(OutboxEventRepositorySql.class);
  private final OutboxEventJpaRepository jpaRepository;
  public OutboxEventRepositorySql(OutboxEventJpaRepository jpaRepository){
    this.jpaRepository = jpaRepository;
  }


  @Override
  public void save(OutboxEvent outboxEvent) {
    jpaRepository.save(OutboxEventMapper.toEntity(outboxEvent));
  }

  @Override
  public List<OutboxEvent> findByStatus(OutboxEventStatus status) {
    List<OutboxEventEntity> outboxEventEntityList = jpaRepository.findByStatus(status);
    return outboxEventEntityList.stream().map(OutboxEventMapper::toDomain).toList();
  }

  @Override
  public List<OutboxEvent> findByQueue(String queue) {
    List<OutboxEventEntity> outboxEventEntityList = jpaRepository.findByQueue(queue);
    return outboxEventEntityList.stream().map(OutboxEventMapper::toDomain).toList();
  }

  @Override
  public List<OutboxEvent> findByRoutingKey(String routingKey) {
    List<OutboxEventEntity> outboxEventEntityList = jpaRepository.findByRoutingKey(routingKey);
    return outboxEventEntityList.stream().map(OutboxEventMapper::toDomain).toList();
  }
}
