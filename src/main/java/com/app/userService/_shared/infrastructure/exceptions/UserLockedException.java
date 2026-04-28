package com.app.userService._shared.infrastructure.exceptions;

public class UserLockedException extends RuntimeException{
  public UserLockedException(String message) {
    super(message);
  }
}
