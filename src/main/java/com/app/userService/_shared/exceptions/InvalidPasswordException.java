package com.app.userService._shared.exceptions;

public class InvalidPasswordException extends RuntimeException{
  public InvalidPasswordException(String message) {
    super(message);
  }
}
