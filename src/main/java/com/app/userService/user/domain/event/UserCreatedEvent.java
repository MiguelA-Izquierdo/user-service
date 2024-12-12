package com.app.userService.user.domain.event;

import java.util.UUID;

public class UserCreatedEvent implements Event<UserCreatedEvent.UserPayload> {

  private final UserPayload payload;

  private UserCreatedEvent(UUID userId, String name, String lastName, String email) {
    this.payload = new UserPayload(userId, name, lastName, email);
  }

  public static UserCreatedEvent of(UUID userId, String name, String lastName, String email) {
    return new UserCreatedEvent(userId, name, lastName, email);
  }

  @Override
  public String getQueue() {
    return "userCreatedQueue";
  }

  @Override
  public String getExchange() {
    return "userExchange";
  }

  @Override
  public String getRoutingKey() {
    return "user.created";
  }

  @Override
  public String getType() {
    return "user.created";
  }

  @Override
  public UserPayload getPayload() {
    return this.payload;
  }

  public record UserPayload(UUID userId, String name, String lastName, String email) {
  }
}
