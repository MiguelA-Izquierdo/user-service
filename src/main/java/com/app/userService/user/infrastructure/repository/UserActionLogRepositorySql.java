package com.app.userService.user.infrastructure.repository;

import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.UserActionLog;
import com.app.userService.user.domain.repositories.UserActionLogRepository;
import com.app.userService.user.infrastructure.entities.UserActionLogEntity;
import com.app.userService.user.infrastructure.entities.UserEntity;
import com.app.userService.user.infrastructure.mapper.UserActionLogMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;


@Repository
public class UserActionLogRepositorySql implements UserActionLogRepository {
  private static final Logger logger = LoggerFactory.getLogger(UserActionLogRepositorySql.class);

  @PersistenceContext
  private EntityManager entityManager;
  private final UserActionLogJpaRepository jpaRepository;
  public UserActionLogRepositorySql(UserActionLogJpaRepository jpaRepository){
    this.jpaRepository = jpaRepository;
  }

  @Override
  public void save(UserActionLog userActionLog) {
    UserEntity userEntity = entityManager.getReference(UserEntity.class, userActionLog.getUser().getId().getValue());
    UserActionLogEntity userActionLogEntity = UserActionLogMapper.toEntity(userActionLog, userEntity);

    jpaRepository.save(userActionLogEntity);
  }


}

