package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserName  extends ValueObjectAbstract {

  private final String value;

  private UserName(String value) {
    this.value = validate(value);
  }

  public static UserName of(String value) {
    return new UserName(value);
  }
  public static <T> Map<String, String> getValidationErrors(Map<String, T> args) {
    HashMap<String, String> errors = new HashMap<>();

    String userName = (String) args.get("userName");

    try {
      validate(userName);
    } catch (ValueObjectValidationException e) {
      errors.put(e.getField(), e.getMessage());
    }

    return errors;
  }
  public String getValue() {
    return value;
  }

  private static String validate(String value) {
    validateNotNullOrEmpty(value, "User name");
    if (value.trim().isEmpty()) {
      throw new ValueObjectValidationException("UserName","User name cannot be empty");
    }
    if (value.length() < 3 || value.length() > 100) {
      throw new ValueObjectValidationException("UserName","The User name must be between 3 and 100 characters long.");
    }

    return value;
  }


  @Override
  public String toString() {
    return value;
  }

  @Override
  protected boolean compareAttributes(Object o) {
    if (!(o instanceof UserName that)) return false;
    return Objects.equals(this.value, that.value);
  }

  @Override
  protected int generateHashCode() {
    return Objects.hash(value);
  }
}
