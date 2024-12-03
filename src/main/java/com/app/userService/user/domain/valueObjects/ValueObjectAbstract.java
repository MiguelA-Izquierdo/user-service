package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;

public abstract  class ValueObjectAbstract {
  protected static void validateNotNullOrEmpty(String value, String fieldName) {
    if (value == null || value.trim().isEmpty()) {
      throw new ValueObjectValidationException(fieldName, fieldName + " cannot be null or empty");
    }
  }
}
