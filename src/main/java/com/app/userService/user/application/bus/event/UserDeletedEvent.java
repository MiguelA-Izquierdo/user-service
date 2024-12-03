package com.app.userService.user.application.bus.event;

import com.app.userService.user.domain.event.UserEvent;
import com.app.userService.user.domain.model.User;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record UserDeletedEvent(UUID userId, Instant timestamp) implements UserEvent, Serializable {

  public static UserDeletedEvent of(User user){
    return new UserDeletedEvent(user.getId().getValue(), Instant.now());
  }
  @Override
  public UUID getUserId() {
    return userId;
  }

  @Override
  public String getRoutingKey() {
    return "user.deleted";
  }
}
