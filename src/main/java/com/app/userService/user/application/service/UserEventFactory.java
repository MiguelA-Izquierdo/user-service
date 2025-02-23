package com.app.userService.user.application.service;

import com.app.userService.auth.domain.service.TokenService;
import com.app.userService.user.domain.event.UserCreatedDomainEvent;
import com.app.userService.user.domain.event.UserDeletedDomainEvent;
import com.app.userService.user.domain.event.UserLockedDomainEvent;
import com.app.userService.user.domain.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserEventFactory {
  @Value("${messaging.exchange.user}")
  private String userExchange;

  @Value("${messaging.queue.userCreated}")
  private String userCreatedQueue;

  @Value("${messaging.queue.userDeleted}")
  private String userDeletedQueue;
  @Value("${messaging.queue.userLocked}")
  private String userLockedQueue;

  @Value("${messaging.routing.key.userCreated}")
  private String userCreatedRoutingKey;

  @Value("${messaging.routing.key.userDeleted}")
  private String userDeletedRoutingKey;
  @Value("${messaging.routing.key.userLocked}")
  private String userLockedRoutingKey;

  public UserCreatedDomainEvent createUserCreatedEvent(User user) {
    return new UserCreatedDomainEvent(
      userExchange,
      userCreatedQueue,
      userCreatedRoutingKey,
      user.getId().getValue(),
      user.getName().getValue(),
      user.getLastName().getValue(),
      user.getEmail().getEmail()
    );
  }

  public UserDeletedDomainEvent createUserDeletedEvent(User user) {
    return new UserDeletedDomainEvent(
      userExchange,
      userDeletedQueue,
      userDeletedRoutingKey,
      user.getId().getValue(),
      user.getName().getValue(),
      user.getLastName().getValue(),
      user.getEmail().getEmail()
    );
  }
  public UserLockedDomainEvent createUserLockedEvent(User user, String token) {

    return new UserLockedDomainEvent(
      userExchange,
      userLockedQueue,
      userLockedRoutingKey,
      user.getId().getValue(),
      user.getName().getValue(),
      user.getLastName().getValue(),
      user.getEmail().getEmail(),
      token,
      LocalDateTime.now()
    );
  }
}
