package com.app.userService.user.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public class UserActionLog {
  private final UUID id;
  private final User user;
  private final UserAction userAction;
  private final Instant timestamp;
  private final Map<String, String> metadata;

  private UserActionLog(UUID id, User user, UserAction userAction, Instant timestamp, Map<String, String> metadata) {
    this.id = id;
    this.user = user;
    this.userAction = userAction;
    this.timestamp = timestamp != null ? timestamp : Instant.now();
    this.metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
  }

  public static UserActionLog of(UUID id, User user, UserAction userAction, Map<String, String> metadata) {
    return new UserActionLog(id, user, userAction, null, metadata);
  }

  public static UserActionLog of(UUID id, User user, UserAction userAction, Instant timestamp, Map<String, String> metadata) {
    return new UserActionLog(id, user, userAction, timestamp, metadata);
  }

  public UUID getId() {
    return id;
  }

  public User getUser() {
    return user;
  }

  public UserAction getUserAction() {
    return userAction;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public Map<String, String> getMetadata() {
    return metadata;
  }

  @Override
  public String toString() {
    return "UserActionLog{" +
      "id=" + id +
      ", user=" + user +
      ", userAction=" + userAction +
      ", timestamp=" + timestamp +
      ", metadata=" + metadata +
      '}';
  }
}
