package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserEmailTest {

  @Test
  void testValidEmail() {
    String validEmail = "user@example.com";
    UserEmail userEmail = UserEmail.of(validEmail);

    assertNotNull(userEmail);
    assertEquals(validEmail, userEmail.getEmail());
  }

  @Test
  void testInvalidEmailWithoutAtSymbol() {
    String invalidEmail = "userexample.com";

    Exception exception = assertThrows(ValueObjectValidationException.class, () -> {
      UserEmail.of(invalidEmail);
    });

    assertEquals("Invalid email address: " + invalidEmail, exception.getMessage());
  }

  @Test
  void testInvalidEmailWithoutDomain() {
    String invalidEmail = "user@.com";

    Exception exception = assertThrows(ValueObjectValidationException.class, () -> {
      UserEmail.of(invalidEmail);
    });

    assertEquals("Invalid email address: " + invalidEmail, exception.getMessage());
  }

  @Test
  void testInvalidEmailWithoutTopLevelDomain() {
    String invalidEmail = "user@example";

    Exception exception = assertThrows(ValueObjectValidationException.class, () -> {
      UserEmail.of(invalidEmail);
    });

    assertEquals("Invalid email address: " + invalidEmail, exception.getMessage());
  }

  @Test
  void testNullEmail() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () -> {
      UserEmail.of(null);
    });

    assertEquals("Invalid email address: null", exception.getMessage());
  }

  @Test
  void testEmptyEmail() {
    String invalidEmail = "";

    Exception exception = assertThrows(ValueObjectValidationException.class, () -> {
      UserEmail.of(invalidEmail);
    });

    assertEquals("Invalid email address: ", exception.getMessage());
  }

  @Test
  void testEqualsAndHashCode() {
    UserEmail email1 = UserEmail.of("user@example.com");
    UserEmail email2 = UserEmail.of("user@example.com");
    UserEmail email3 = UserEmail.of("different@example.com");

    assertEquals(email1, email2);
    assertNotEquals(email1, email3);
    assertEquals(email1.hashCode(), email2.hashCode());
    assertNotEquals(email1.hashCode(), email3.hashCode());
  }

  @Test
  void testToString() {
    UserEmail userEmail = UserEmail.of("user@example.com");
    assertEquals("user@example.com", userEmail.toString());
  }
}
