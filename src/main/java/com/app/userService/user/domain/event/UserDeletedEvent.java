package com.app.userService.user.domain.event;

import java.util.UUID;

public class UserDeletedEvent implements Event<UserDeletedEvent.UserPayload> {

  private final String userExchange;
  private final String userDeletedQueue;
  private final String userDeletedRoutingKey;
  private final UserPayload payload;

  public UserDeletedEvent(String userExchange,
                          String userDeletedQueue,
                          String userDeletedRoutingKey,
                          UUID userId, String name, String lastName, String email) {
    this.userExchange = userExchange;
    this.userDeletedQueue = userDeletedQueue;
    this.userDeletedRoutingKey = userDeletedRoutingKey;
    this.payload = new UserDeletedEvent.UserPayload(userId, name, lastName, email);
  }

  @Override
  public String getQueue() {
    return userDeletedQueue;
  }

  @Override
  public String getExchange() {
    return userExchange;
  }

  @Override
  public String getRoutingKey() {
    return userDeletedRoutingKey;
  }

  @Override
  public String getType() {
    return "user.deleted";
  }

  @Override
  public UserPayload getPayload() {
    return this.payload;
  }

  public record UserPayload(UUID userId, String name, String lastName, String email) {}
}
