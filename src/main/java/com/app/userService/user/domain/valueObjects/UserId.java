package com.app.userService.user.domain.valueObjects;

  import com.app.userService.user.domain.exceptions.ValueObjectValidationException;

  import java.util.HashMap;
  import java.util.Map;
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
    validateNotNullOrEmpty(value,"UserId");
    try {
      UUID uuid = UUID.fromString(value);
      validateVersion(uuid);
      return new UserId(uuid);
    } catch (IllegalArgumentException e) {
      throw new ValueObjectValidationException("UserId","The provided UserId is not a valid UUID: " + value);
    }
  }

  public static <T> Map<String, String> getValidationErrors(Map<String, T> args) {
    HashMap<String, String> errors = new HashMap<>();

    String userId = (String) args.get("userId");

    try {
      UserId.of(userId);
    } catch (ValueObjectValidationException e) {
      errors.put(e.getField(), e.getMessage());
    }

    return errors;
  }
  public UUID getValue() {
    return value;
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

  @Override
  protected boolean compareAttributes(Object o) {
    if (!(o instanceof UserId that)) return false;
    return Objects.equals(this.value, that.value);
  }

  @Override
  protected int generateHashCode() {
    return Objects.hash(value);
  }
}
