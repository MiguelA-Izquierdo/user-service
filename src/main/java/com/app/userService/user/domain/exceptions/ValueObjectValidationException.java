package com.app.userService.user.domain.exceptions;


public class ValueObjectValidationException extends RuntimeException {
  private final String field;

  public ValueObjectValidationException(String field, String message) {
    super(message);
    this.field = field;
  }

  public String getField() {
    return field;
  }


}
