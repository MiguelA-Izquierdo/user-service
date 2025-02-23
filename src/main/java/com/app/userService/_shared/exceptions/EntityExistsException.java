package com.app.userService._shared.exceptions;

public class EntityExistsException extends RuntimeException {
  public EntityExistsException(String message) {
    super(message);
  }
}
