package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserNameTest {

  @Test
  void testValidUserName() {
    UserName userName = UserName.of("John");
    assertNotNull(userName);
    assertEquals("John", userName.getValue());
  }

  @Test
  void testMinimumLengthUserName() {
    UserName userName = UserName.of("Amy");
    assertNotNull(userName);
    assertEquals("Amy", userName.getValue());
  }

  @Test
  void testMaximumLengthUserName() {
    String longUserName = "a".repeat(100);
    UserName userName = UserName.of(longUserName);
    assertNotNull(userName);
    assertEquals(longUserName, userName.getValue());
  }

  @Test
  void testUserNameTooShort() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      UserName.of("Al")
    );
    assertEquals("The User name must be between 3 and 100 characters long.", exception.getMessage());
  }

  @Test
  void testUserNameTooLong() {
    String tooLongUserName = "a".repeat(101);
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      UserName.of(tooLongUserName)
    );
    assertEquals("The User name must be between 3 and 100 characters long.", exception.getMessage());
  }

  @Test
  void testNullUserName() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      UserName.of(null)
    );
    assertEquals("User name cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEmptyUserName() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      UserName.of("")
    );
    assertEquals("User name cannot be null or empty", exception.getMessage());
  }

  @Test
  void testWhitespaceUserName() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      UserName.of("   ")
    );
    assertEquals("User name cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEqualsAndHashCode() {
    UserName userName1 = UserName.of("John");
    UserName userName2 = UserName.of("John");
    UserName userName3 = UserName.of("Jane");

    assertEquals(userName1, userName2);
    assertNotEquals(userName1, userName3);
    assertEquals(userName1.hashCode(), userName2.hashCode());
    assertNotEquals(userName1.hashCode(), userName3.hashCode());
  }

  @Test
  void testToString() {
    UserName userName = UserName.of("Alex");
    assertEquals("Alex", userName.toString());
  }
}
