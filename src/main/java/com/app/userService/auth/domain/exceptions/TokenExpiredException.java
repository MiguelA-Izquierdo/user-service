package com.app.userService.auth.domain.exceptions;
public class TokenExpiredException extends RuntimeException{
  public TokenExpiredException(String message) {
    super(message);
  }
}
