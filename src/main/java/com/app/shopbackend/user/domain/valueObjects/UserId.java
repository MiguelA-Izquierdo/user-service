package com.app.shopbackend.user.domain.valueObjects;

  import java.util.Objects;
  import java.util.UUID;

public class UserId {
  private final UUID value;

  public UserId(UUID value) {
    this.value = Objects.requireNonNull(value, "UserId cannot be null");
    validateVersion(value);
  }

  public static UserId of(UUID value) {
    return new UserId(value);
  }

  public static UserId of(String value) {
    Objects.requireNonNull(value, "UserId cannot be null");
    try {
      UUID uuid = UUID.fromString(value);
      validateVersion(uuid);
      return new UserId(uuid);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("The provided UserId is not a valid UUID: " + value, e);
    }
  }

  public UUID getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserId userId = (UserId) o;
    return value.equals(userId.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value.toString();
  }

  private static void validateVersion(UUID uuid) {
    if (uuid.version() != 4) {
      throw new IllegalArgumentException("The provided UUID is not a version 4 UUID");
    }
  }
}
