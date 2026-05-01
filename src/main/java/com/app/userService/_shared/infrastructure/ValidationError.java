package com.app.userService._shared.infrastructure;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import java.util.HashMap;
import java.util.Map;

public class ValidationError extends RuntimeException {
  private final Map<String, Map<String, String>> errors = new HashMap<>();

  public void validate(String fieldName, Runnable validation) {
    try {
      validation.run();
    } catch (ValueObjectValidationException e) {
      addError(fieldName, e.getField(), e.getMessage());
    }
  }

  public void addError(String field, String subField, String message) {
    errors.putIfAbsent(field, new HashMap<>());

    errors.get(field).put(subField, message);
  }
  public Map<String, Map<String, String>> getErrors() {
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
