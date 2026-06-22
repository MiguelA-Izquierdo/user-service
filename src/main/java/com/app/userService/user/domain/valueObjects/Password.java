package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;

import java.util.regex.Pattern;


public class Password extends ValueObjectAbstract{
  private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{12,}$";
  private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
  private static final String VALIDATION_MESSAGE = "Password must be at least 12 characters and include uppercase, lowercase, digit, and special character.";

  private final String password;
  private Password(String password) {
    if (!isValid(password)) {
      throw new ValueObjectValidationException("password", VALIDATION_MESSAGE);
    }
    this.password = password;
  }

  public static Password of(String password) {
    return new Password(password);
  }
  private static boolean isValid(String password) {
    if (password == null || password.isEmpty()) {
      return false;
    }
    return PASSWORD_PATTERN.matcher(password).matches();
  }

  public String getPassword() {
    return password;
  }

  @Override
  public String toString() {
    return "[PROTECTED]";
  }

  @Override
  protected boolean compareAttributes(Object o) {
    if (!(o instanceof Password that)) return false;
    return this.password.equals(that.password);
  }

  @Override
  protected int generateHashCode() {
    return password.hashCode();
  }

}
