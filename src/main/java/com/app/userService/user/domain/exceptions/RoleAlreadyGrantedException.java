package com.app.userService.user.domain.exceptions;

public class RoleAlreadyGrantedException extends RuntimeException{
  public RoleAlreadyGrantedException(String message) {
    super(message);
  }
}