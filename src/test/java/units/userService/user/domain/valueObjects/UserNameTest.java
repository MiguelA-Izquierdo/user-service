package units.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import com.app.userService.user.domain.valueObjects.UserName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class UserNameTest {

  @Test
  void testValidUserName() {
    UserName userName = UserName.of("John");
    Assertions.assertNotNull(userName);
    Assertions.assertEquals("John", userName.getValue());
  }

  @Test
  void testMinimumLengthUserName() {
    UserName userName = UserName.of("Amy");
    Assertions.assertNotNull(userName);
    Assertions.assertEquals("Amy", userName.getValue());
  }

  @Test
  void testMaximumLengthUserName() {
    String longUserName = "a".repeat(100);
    UserName userName = UserName.of(longUserName);
    Assertions.assertNotNull(userName);
    Assertions.assertEquals(longUserName, userName.getValue());
  }

  @Test
  void testUserNameTooShort() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      UserName.of("Al")
    );
    Assertions.assertEquals("The User name must be between 3 and 100 characters long.", exception.getMessage());
  }

  @Test
  void testUserNameTooLong() {
    String tooLongUserName = "a".repeat(101);
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      UserName.of(tooLongUserName)
    );
    Assertions.assertEquals("The User name must be between 3 and 100 characters long.", exception.getMessage());
  }

  @Test
  void testNullUserName() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      UserName.of(null)
    );
    Assertions.assertEquals("User name cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEmptyUserName() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      UserName.of("")
    );
    Assertions.assertEquals("User name cannot be null or empty", exception.getMessage());
  }

  @Test
  void testWhitespaceUserName() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      UserName.of("   ")
    );
    Assertions.assertEquals("User name cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEqualsAndHashCode() {
    UserName userName1 = UserName.of("John");
    UserName userName2 = UserName.of("John");
    UserName userName3 = UserName.of("Jane");

    Assertions.assertEquals(userName1, userName2);
    Assertions.assertNotEquals(userName1, userName3);
    Assertions.assertEquals(userName1.hashCode(), userName2.hashCode());
    Assertions.assertNotEquals(userName1.hashCode(), userName3.hashCode());
  }

  @Test
  void testToString() {
    UserName userName = UserName.of("Alex");
    Assertions.assertEquals("Alex", userName.toString());
  }
}
