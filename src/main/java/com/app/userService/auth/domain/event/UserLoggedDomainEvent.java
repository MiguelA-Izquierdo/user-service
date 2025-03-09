package com.app.userService.auth.domain.event;

import com.app.userService._shared.domain.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class UserLoggedDomainEvent implements Event<UserLoggedDomainEvent.UserPayload> {

  private static final Logger logger = LoggerFactory.getLogger(UserLoggedDomainEvent.class);

  private final String userExchange;
  private final String userLoggedQueue;
  private final String userLoggedRoutingKey;
  private final UserPayload payload;

  public UserLoggedDomainEvent(String userExchange,
                               String userLoggedQueue,
                               String userLoggedRoutingKey,
                               UUID userId,
                               String name,
                               String lastName,
                               String email) {
    this.userExchange = userExchange;
    this.userLoggedQueue = userLoggedQueue;
    this.userLoggedRoutingKey = userLoggedRoutingKey;
    this.payload = new UserPayload(userId, name, lastName, email);
  }

  @Override
  public String getQueue() {
    return userLoggedQueue;
  }

  @Override
  public String getExchange() {
    return userExchange;
  }

  @Override
  public String getRoutingKey() {
    return userLoggedRoutingKey;
  }

  @Override
  public String getType() {
    return "user.logged";
  }

  @Override
  public UserPayload getPayload() {
    return this.payload;
  }

  public record UserPayload(UUID userId, String name, String lastName, String email) {}
}
