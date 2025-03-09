package com.app.userService._shared.infraestructure.exceptions;


import java.util.List;

public class JwtValidationError extends RuntimeException {
  private List<String> errors;

  public JwtValidationError(List<String> errors) {
    this.errors = errors;
  }

  public List<String> getErrors() {
    return errors;
  }
}
