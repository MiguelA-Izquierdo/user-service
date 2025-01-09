package com.app.userService.user.domain.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class UserCreatedDomainEvent implements Event<UserCreatedDomainEvent.UserPayload> {

  private static final Logger logger = LoggerFactory.getLogger(UserCreatedDomainEvent.class);

  private final String userExchange;
  private final String userCreatedQueue;
  private final String userCreatedRoutingKey;
  private final UserPayload payload;

  public UserCreatedDomainEvent(String userExchange,
                                String userCreatedQueue,
                                String userCreatedRoutingKey,
                                UUID userId,
                                String name,
                                String lastName,
                                String email) {
    this.userExchange = userExchange;
    this.userCreatedQueue = userCreatedQueue;
    this.userCreatedRoutingKey = userCreatedRoutingKey;
    this.payload = new UserPayload(userId, name, lastName, email);
  }

  @Override
  public String getQueue() {
    return userCreatedQueue;
  }

  @Override
  public String getExchange() {
    return userExchange;
  }

  @Override
  public String getRoutingKey() {
    return userCreatedRoutingKey;
  }

  @Override
  public String getType() {
    return "user.created";
  }

  @Override
  public UserPayload getPayload() {
    return this.payload;
  }

  public record UserPayload(UUID userId, String name, String lastName, String email) {}
}
