package com.app.userService._shared.infraestructure;

import java.util.HashMap;
import java.util.Map;

public class ValidationError extends RuntimeException {
  private final Map<String, String> errors = new HashMap<>();

  public void addError(String field, String message) {
    errors.put(field, message);
  }

  public Map<String, String> getErrors() {
    return errors;
  }

  public boolean hasErrors() {
    return !errors.isEmpty();
  }

  @Override
  public String getMessage() {
    return "Validation errors: " + errors.toString();
  }
}
