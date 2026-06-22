package units.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import com.app.userService.user.domain.valueObjects.Password;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PasswordTest {

  private static final String ERROR_MSG =
      "Password must be at least 12 characters and include uppercase, lowercase, digit, and special character.";

  @Test
  void testValidPassword() {
    Password password = Password.of("SecurePass@12");
    Assertions.assertNotNull(password);
    Assertions.assertEquals("SecurePass@12", password.getPassword());
  }

  @Test
  void testPasswordWithoutUppercase() {
    ValueObjectValidationException exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      Password.of("securepass@12")
    );
    Assertions.assertEquals("password", exception.getField());
    Assertions.assertEquals(ERROR_MSG, exception.getMessage());
  }

  @Test
  void testPasswordWithoutLowercase() {
    ValueObjectValidationException exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      Password.of("SECUREPASS@12")
    );
    Assertions.assertEquals("password", exception.getField());
    Assertions.assertEquals(ERROR_MSG, exception.getMessage());
  }

  @Test
  void testPasswordWithoutDigit() {
    ValueObjectValidationException exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      Password.of("SecurePass@ab")
    );
    Assertions.assertEquals("password", exception.getField());
    Assertions.assertEquals(ERROR_MSG, exception.getMessage());
  }

  @Test
  void testPasswordWithoutSpecialCharacter() {
    ValueObjectValidationException exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      Password.of("SecurePass123")
    );
    Assertions.assertEquals("password", exception.getField());
    Assertions.assertEquals(ERROR_MSG, exception.getMessage());
  }

  @Test
  void testPasswordTooShort() {
    ValueObjectValidationException exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      Password.of("Secure@12")
    );
    Assertions.assertEquals("password", exception.getField());
    Assertions.assertEquals(ERROR_MSG, exception.getMessage());
  }

  @Test
  void testNullPassword() {
    ValueObjectValidationException exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      Password.of(null)
    );
    Assertions.assertEquals("password", exception.getField());
    Assertions.assertEquals(ERROR_MSG, exception.getMessage());
  }

  @Test
  void testEmptyPassword() {
    ValueObjectValidationException exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      Password.of("")
    );
    Assertions.assertEquals("password", exception.getField());
    Assertions.assertEquals(ERROR_MSG, exception.getMessage());
  }

  @Test
  void testEqualsAndHashCode() {
    Password password1 = Password.of("SecurePass@12");
    Password password2 = Password.of("SecurePass@12");
    Password password3 = Password.of("Different@456X");

    Assertions.assertEquals(password1, password2);
    Assertions.assertNotEquals(password1, password3);
    Assertions.assertEquals(password1.hashCode(), password2.hashCode());
    Assertions.assertNotEquals(password1.hashCode(), password3.hashCode());
  }

  @Test
  void testToString() {
    Password password = Password.of("SecurePass@12");
    Assertions.assertEquals("[PROTECTED]", password.toString());
  }
}