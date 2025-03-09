package com.app.userService._shared.infraestructure.exceptions;

public class UserLockedException extends RuntimeException{
  public UserLockedException(String message) {
    super(message);
  }
}
