package com.app.userService.user.application.bus.event;

import com.app.userService.user.domain.event.UserEvent;
import com.app.userService.user.domain.model.User;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record UserCreatedEvent(UUID userId, String name, String lastName, String email, Instant timestamp) implements UserEvent, Serializable {

  public static UserCreatedEvent of(User user){
    return new UserCreatedEvent(user.getId().getValue(),
      user.getName().getValue(),
      user.getLastName().getValue(),
      user.getEmail().getEmail(),
      Instant.now()
    );
  }
  @Override
  public UUID getUserId() {
    return userId;
  }

  @Override
  public String getRoutingKey() {
    return "user.created";
  }
}
