package com.app.userService.user.domain.valueObjects;

  import com.app.userService.user.domain.exceptions.ValueObjectValidationException;

  import java.util.Objects;
  import java.util.UUID;

public class UserId extends ValueObjectAbstract{
  private final UUID value;

  public UserId(UUID value) {
    this.value = Objects.requireNonNull(value, "UserId cannot be null");
    validateVersion(value);
  }

  public static UserId of(UUID value) {
    return new UserId(value);
  }

  public static UserId of(String value) {
    validateNotNullOrEmpty(value, "UserId");
    try {
      UUID uuid = UUID.fromString(value);
      validateVersion(uuid);
      return new UserId(uuid);
    } catch (IllegalArgumentException e) {
      throw new ValueObjectValidationException("UserId","The provided UserId is not a valid UUID: " + value);
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
      throw new ValueObjectValidationException("UserId","The provided UUID is not a version 4 UUID");
    }
  }
}
