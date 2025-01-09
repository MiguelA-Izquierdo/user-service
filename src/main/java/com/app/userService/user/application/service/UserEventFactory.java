package com.app.userService.user.application.service;

import com.app.userService.user.domain.event.UserCreatedDomainEvent;
import com.app.userService.user.domain.event.UserDeletedDomainEvent;
import com.app.userService.user.domain.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UserEventFactory {

  @Value("${messaging.exchange.user}")
  private String userExchange;

  @Value("${messaging.queue.userCreated}")
  private String userCreatedQueue;

  @Value("${messaging.queue.userDeleted}")
  private String userDeletedQueue;

  @Value("${messaging.routing.key.userCreated}")
  private String userCreatedRoutingKey;

  @Value("${messaging.routing.key.userDeleted}")
  private String userDeletedRoutingKey;

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
}
