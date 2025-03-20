package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;


public abstract class ValueObjectAbstract {

  protected static void validateNotNullOrEmpty(String value, String fieldName) {
    if (value == null || value.trim().isEmpty()) {
      throw new ValueObjectValidationException(fieldName, fieldName + " cannot be null or empty");
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return compareAttributes(o);
  }

  protected abstract boolean compareAttributes(Object o);

  @Override
  public int hashCode() {
    return generateHashCode();
  }

  protected abstract int generateHashCode();
}
