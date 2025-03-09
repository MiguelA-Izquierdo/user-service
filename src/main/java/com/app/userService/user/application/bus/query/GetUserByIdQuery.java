package com.app.userService.user.application.bus.query;

import com.app.userService._shared.application.bus.query.Query;
import com.app.userService.user.domain.model.User;

import java.util.Optional;
import java.util.UUID;


public class GetUserByIdQuery implements Query<Optional<User>> {
  private final UUID id;

  public GetUserByIdQuery(String id){
    this.id = convertToUUID(id);
  }

  private static UUID convertToUUID(String id) {
    if (id == null || id.isEmpty()) {
      throw new IllegalArgumentException("UserId cannot be null or empty");
    }
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid UserId, UUID format: " + id, e);
    }
  }

  public UUID getId() {
    return id;
  }
}
