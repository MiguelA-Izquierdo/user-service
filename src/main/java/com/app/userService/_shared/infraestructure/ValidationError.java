package com.app.userService._shared.infraestructure;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ValidationError extends RuntimeException {
  private final Map<String, Map<String, String>> errors = new HashMap<>();

  public void addError(String field, String subField, String message) {
    errors.putIfAbsent(field, new HashMap<>());

    errors.get(field).put(subField, message);
  }
  public  <T> void validateField(
    String fieldMainName,
    Map<String, T> fields,
    Function<Map<String, T>, Map<String, String>> validator,
    Boolean isRequired) {

    if (!fields.isEmpty() || isRequired) {
      Map<String, String> errors = validator.apply(fields);
      errors.forEach((field, value) -> addError(fieldMainName, field, value));
    }
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
