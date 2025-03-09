package com.app.userService.user.application.service;

import com.app.userService._shared.application.service.RequestContext;
import com.app.userService.user.domain.model.*;
import com.app.userService.user.domain.repositories.UserActionLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequestScope
public class UserActionLogService {
  private HashMap<String, String> metaData = new HashMap<>();
  private final UserActionLogRepository userActionLogRepository;
  private final RequestContext requestContext;

  public UserActionLogService(UserActionLogRepository userActionLogRepository, RequestContext requestContext) {
    this.userActionLogRepository = userActionLogRepository;
    this.requestContext = requestContext;
  }

  public void registerUserAction(User user, UserAction action) {
    Map<String, String> mergedMetaData = new HashMap<>(requestContext.getMetaData());
    mergedMetaData.putAll(metaData);

    userActionLogRepository.save(
      UserActionLog.of(UUID.randomUUID(), user, action, mergedMetaData)
    );
    clearMetadata();
  }
  public void addMetadata(String key, String value){
    metaData.put(key, value);
  }
  private void clearMetadata(){
    metaData = new HashMap<>();
  }
}
