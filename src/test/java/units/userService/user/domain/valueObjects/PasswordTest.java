package units.userService.user.domain.valueObjects;

import com.app.userService.user.domain.valueObjects.Password;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PasswordTest {

  @Test
  void testValidPassword() {
    Password password = Password.of("Secure@123");
    Assertions.assertNotNull(password);
    Assertions.assertEquals("Secure@123", password.toString());
  }

  @Test
  void testPasswordWithoutUppercase() {
    Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
      Password.of("secure@123")
    );
    Assertions.assertEquals("Password must be at least 9 characters long, include one uppercase letter, and one special character.", exception.getMessage());
  }

  @Test
  void testPasswordWithoutSpecialCharacter() {
    Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
      Password.of("Secure123")
    );
    Assertions.assertEquals("Password must be at least 9 characters long, include one uppercase letter, and one special character.", exception.getMessage());
  }

  @Test
  void testPasswordTooShort() {
    Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
      Password.of("S@123")
    );
    Assertions.assertEquals("Password must be at least 9 characters long, include one uppercase letter, and one special character.", exception.getMessage());
  }

  @Test
  void testNullPassword() {
    Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
      Password.of(null)
    );
    Assertions.assertEquals("Password must be at least 9 characters long, include one uppercase letter, and one special character.", exception.getMessage());
  }

  @Test
  void testEmptyPassword() {
    Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () ->
      Password.of("")
    );
    Assertions.assertEquals("Password must be at least 9 characters long, include one uppercase letter, and one special character.", exception.getMessage());
  }

  @Test
  void testEqualsAndHashCode() {
    Password password1 = Password.of("Secure@123");
    Password password2 = Password.of("Secure@123");
    Password password3 = Password.of("Different@456");

    Assertions.assertEquals(password1, password2);
    Assertions.assertNotEquals(password1, password3);
    Assertions.assertEquals(password1.hashCode(), password2.hashCode());
    Assertions.assertNotEquals(password1.hashCode(), password3.hashCode());
  }

  @Test
  void testToString() {
    Password password = Password.of("Secure@123");
    Assertions.assertEquals("Secure@123", password.toString());
  }
}
