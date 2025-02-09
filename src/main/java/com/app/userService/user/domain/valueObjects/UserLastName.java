package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserLastName extends ValueObjectAbstract{

  private final String value;

  private UserLastName(String value) {
    this.value = validate(value);
  }

  public static UserLastName of(String value) {
    return new UserLastName(value);
  }
  public static <T> Map<String, String> getValidationErrors(Map<String, T> args) {
    HashMap<String, String> errors = new HashMap<>();

    String lastName = (String) args.get("lastName");

    try {
     validate(lastName);
    } catch (ValueObjectValidationException e) {
      errors.put(e.getField(), e.getMessage());
    }

    return errors;
  }
  public String getValue() {
    return value;
  }

  private static String validate(String value) {
    validateNotNullOrEmpty(value, "User last");
    if (value.trim().isEmpty()) {
      throw new ValueObjectValidationException("UserLastName","User last name cannot be empty");
    }
    if (value.length() < 3 || value.length() > 100) {
      throw new ValueObjectValidationException("UserLastName","The User last name must be between 3 and 100 characters long.");
    }

    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserLastName that = (UserLastName) o;
    return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return value;
  }
}
