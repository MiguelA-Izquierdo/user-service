package com.app.userService.user.domain.valueObjects;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordTest {

  @Test
  void testValidPassword() {
    Password password = Password.of("Secure@123");
    assertNotNull(password);
    assertEquals("Secure@123", password.toString());
  }

  @Test
  void testPasswordWithoutUppercase() {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
      Password.of("secure@123")
    );
    assertEquals("Password must be at least 9 characters long, include one uppercase letter, and one special character.", exception.getMessage());
  }

  @Test
  void testPasswordWithoutSpecialCharacter() {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
      Password.of("Secure123")
    );
    assertEquals("Password must be at least 9 characters long, include one uppercase letter, and one special character.", exception.getMessage());
  }

  @Test
  void testPasswordTooShort() {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
      Password.of("S@123")
    );
    assertEquals("Password must be at least 9 characters long, include one uppercase letter, and one special character.", exception.getMessage());
  }

  @Test
  void testNullPassword() {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
      Password.of(null)
    );
    assertEquals("Password must be at least 9 characters long, include one uppercase letter, and one special character.", exception.getMessage());
  }

  @Test
  void testEmptyPassword() {
    Exception exception = assertThrows(IllegalArgumentException.class, () ->
      Password.of("")
    );
    assertEquals("Password must be at least 9 characters long, include one uppercase letter, and one special character.", exception.getMessage());
  }

  @Test
  void testEqualsAndHashCode() {
    Password password1 = Password.of("Secure@123");
    Password password2 = Password.of("Secure@123");
    Password password3 = Password.of("Different@456");

    assertEquals(password1, password2);
    assertNotEquals(password1, password3);
    assertEquals(password1.hashCode(), password2.hashCode());
    assertNotEquals(password1.hashCode(), password3.hashCode());
  }

  @Test
  void testToString() {
    Password password = Password.of("Secure@123");
    assertEquals("Secure@123", password.toString());
  }
}
