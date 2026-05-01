package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;

import java.util.regex.Pattern;


public class UserEmail extends ValueObjectAbstract{
  private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
  private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

  private final String email;
  private UserEmail(String email) {
    if (!isValid(email)) {
      throw new ValueObjectValidationException("Email","Invalid email address: " + email);
    }
    this.email = email;
  }

  public static UserEmail of(String email) {
    return new UserEmail(email);
  }
  private static boolean isValid(String email) {
    return email != null && EMAIL_PATTERN.matcher(email).matches();
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String toString() {
    return email;
  }

  @Override
  protected boolean compareAttributes(Object o) {
    if (!(o instanceof UserEmail that)) return false;
    return this.email.equals(that.email);
  }

  @Override
  protected int generateHashCode() {
    return email.hashCode();
  }
}
