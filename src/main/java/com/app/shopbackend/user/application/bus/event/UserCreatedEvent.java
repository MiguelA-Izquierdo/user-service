package com.app.shopbackend.user.application.bus.event;

import com.app.shopbackend.user.domain.event.UserEvent;
import com.app.shopbackend.user.domain.model.User;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record UserCreatedEvent(UUID id, String name, String lastName, String email, Instant timestamp) implements UserEvent, Serializable {

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
    return id;
  }

  @Override
  public String getRoutingKey() {
    return "user.created";
  }
}
