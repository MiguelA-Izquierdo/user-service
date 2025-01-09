package com.app.userService.user.domain.model;

import java.util.Optional;

public class UserWrapper {
  private final boolean exists;
  private final boolean active;
  private final User user;

  private UserWrapper(boolean exists, boolean active, User user) {
    this.exists = exists;
    this.active = active;
    this.user = user;
  }

  public static UserWrapper active(User user) {
    return new UserWrapper(true, true, user);
  }

  public static UserWrapper inactive() {
    return new UserWrapper(true, false, null);
  }

  public static UserWrapper notFound() {
    return new UserWrapper(false, false, null);
  }

  public boolean exists() {
    return exists;
  }

  public boolean isActive() {
    return exists && active;
  }

  public Optional<User> getUser() {
    return Optional.ofNullable(user);
  }
}

