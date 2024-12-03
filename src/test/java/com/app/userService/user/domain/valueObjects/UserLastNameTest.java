package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserLastNameTest {

  @Test
  void testValidUserLastName() {
    UserLastName userLastName = UserLastName.of("Smith");
    assertNotNull(userLastName);
    assertEquals("Smith", userLastName.getValue());
  }

  @Test
  void testMinimumLengthUserLastName() {
    UserLastName userLastName = UserLastName.of("Lee");
    assertNotNull(userLastName);
    assertEquals("Lee", userLastName.getValue());
  }

  @Test
  void testMaximumLengthUserLastName() {
    String longLastName = "a".repeat(100);
    UserLastName userLastName = UserLastName.of(longLastName);
    assertNotNull(userLastName);
    assertEquals(longLastName, userLastName.getValue());
  }

  @Test
  void testUserLastNameTooShort() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      UserLastName.of("Al")
    );
    assertEquals("The User last name must be between 3 and 100 characters long.", exception.getMessage());
  }

  @Test
  void testUserLastNameTooLong() {
    String tooLongLastName = "a".repeat(101);
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      UserLastName.of(tooLongLastName)
    );
    assertEquals("The User last name must be between 3 and 100 characters long.", exception.getMessage());
  }

  @Test
  void testNullUserLastName() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      UserLastName.of(null)
    );
    assertEquals("User last cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEmptyUserLastName() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      UserLastName.of("")
    );
    assertEquals("User last cannot be null or empty", exception.getMessage());
  }

  @Test
  void testWhitespaceUserLastName() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      UserLastName.of("   ")
    );
    assertEquals("User last cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEqualsAndHashCode() {
    UserLastName lastName1 = UserLastName.of("Johnson");
    UserLastName lastName2 = UserLastName.of("Johnson");
    UserLastName lastName3 = UserLastName.of("Doe");

    assertEquals(lastName1, lastName2);
    assertNotEquals(lastName1, lastName3);
    assertEquals(lastName1.hashCode(), lastName2.hashCode());
    assertNotEquals(lastName1.hashCode(), lastName3.hashCode());
  }

  @Test
  void testToString() {
    UserLastName userLastName = UserLastName.of("Brown");
    assertEquals("Brown", userLastName.toString());
  }
}
