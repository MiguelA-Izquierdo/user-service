package com.app.shopbackend.user.domain.valueObjects;

import java.util.regex.Pattern;


public class Password {
  private static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{9,}$";;
  private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

  private final String password;
  private Password(String password) {
    if (!isValid(password)) {
      throw new IllegalArgumentException("Password must be at least 9 characters long, include one uppercase letter, and one special character." );
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

  public String getEmail() {
    return password;
  }

  @Override
  public String toString() {
    return password;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Password that = (Password) obj;
    return password.equals(that.password);
  }

  @Override
  public int hashCode() {
    return password.hashCode();
  }
}
