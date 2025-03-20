package com.app.userService.auth.domain.event;

import com.app.userService._shared.domain.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class UserUnlockedDomainEvent implements Event<UserUnlockedDomainEvent.UserPayload> {

  private static final Logger logger = LoggerFactory.getLogger(UserUnlockedDomainEvent.class);

  private final String userExchange;
  private final String userCreatedQueue;
  private final String userCreatedRoutingKey;
  private final UserPayload payload;

  public UserUnlockedDomainEvent(String userExchange,
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
    return "user.unlocked";
  }

  @Override
  public UserPayload getPayload() {
    return this.payload;
  }

  public record UserPayload(UUID userId, String name, String lastName, String email) {}
}
