package com.app.userService._shared.application.service;

import com.app.userService.auth.domain.event.UserLoggedDomainEvent;
import com.app.userService.auth.domain.event.UserLogoutDomainEvent;
import com.app.userService.user.domain.event.UserCreatedDomainEvent;
import com.app.userService.user.domain.event.UserDeletedDomainEvent;
import com.app.userService.auth.domain.event.UserLockedDomainEvent;
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
  @Value("${messaging.queue.userLogged}")
  private String userLoggedQueue;
  @Value("${messaging.routing.key.userCreated}")
  private String userCreatedRoutingKey;
  @Value("${messaging.routing.key.userDeleted}")
  private String userDeletedRoutingKey;
  @Value("${messaging.routing.key.userLocked}")
  private String userLockedRoutingKey;
  @Value("${messaging.routing.key.userLogged.with.token}")
  private String userLoggedWithTokenRoutingKey;
  @Value("${messaging.routing.key.userLogged.without.token}")
  private String userLoggedWithoutTokenRoutingKey;
  @Value("${messaging.routing.key.userLogged.logout}")
  private String userLogoutRoutingKey;

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
  public UserLoggedDomainEvent createUserLoggedEvent(User user, boolean isUsingToken) {
    String routingKey = isUsingToken ? userLoggedWithTokenRoutingKey : userLoggedWithoutTokenRoutingKey;
    return new UserLoggedDomainEvent(
      userExchange,
      userLoggedQueue,
      routingKey,
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
  public UserLogoutDomainEvent createUserLogoutEvent(User user) {

    return new UserLogoutDomainEvent(
      userExchange,
      userLoggedQueue,
      userLogoutRoutingKey,
      user.getId().getValue(),
      user.getName().getValue(),
      user.getLastName().getValue(),
      user.getEmail().getEmail()
    );
  }
}
