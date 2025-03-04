package com.app.userService.user.application.service;

import com.app.userService._shared.application.service.RequestContext;
import com.app.userService.user.domain.event.UserCreatedDomainEvent;
import com.app.userService.user.domain.event.UserDeletedDomainEvent;
import com.app.userService.user.domain.model.*;
import com.app.userService.user.domain.repositories.OutboxEventRepository;
import com.app.userService.user.domain.repositories.UserActionLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UserActionLogService {

  private final UserActionLogRepository userActionLogRepository;
  private final RequestContext requestContext;

  public UserActionLogService(UserActionLogRepository userActionLogRepository,  RequestContext requestContext) {
    this.userActionLogRepository = userActionLogRepository;
    this.requestContext = requestContext;

  }
  public void registerUserCreated(User user, Map<String, String> metaData){
    this.createUserActionLog(user, UserAction.CREATED, metaData);
  }
  public void registerUserDeleted(User user, Map<String, String> metaData){
    this.createUserActionLog(user, UserAction.DELETED, metaData);
  }
  public void registerLoginWithoutToken(User user, Map<String, String> metaData){
    this.createUserActionLog(user, UserAction.LOGGED, metaData);
  }
  public void registerLogout(User user, Map<String, String> metaData){
    this.createUserActionLog(user, UserAction.LOGOUT, metaData);
  }
  public void registerUserLocked(User user, Map<String, String> metaData){
    this.createUserActionLog(user, UserAction.LOCKED, metaData);
  }
  public void registerUserUnlocked(User user, Map<String, String> metaData){
    this.createUserActionLog(user, UserAction.UNLOCKED, metaData);
  }
  public void registerLoginWithToken(User user, Map<String, String> metaData){
    this.createUserActionLog(user, UserAction.LOGGED_WITH_TOKEN, metaData);
  }
  private void createUserActionLog(User user, UserAction action, Map<String, String> metaData){
    Map<String, String> requestMetaData = requestContext.getMetaData();
    Map<String, String> mergedMetaData = new HashMap<>(requestMetaData);
    mergedMetaData.putAll(metaData);

    UserActionLog userActionLog = UserActionLog.of(
      UUID.randomUUID(),
      user,
      action,
      mergedMetaData
    );
    userActionLogRepository.save(userActionLog);
  }
}
