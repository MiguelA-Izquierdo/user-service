package units.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import com.app.userService.user.domain.valueObjects.UserEmail;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
class UserEmailTest {

  @Test
  void testValidEmail() {
    String validEmail = "user@example.com";
    UserEmail userEmail = UserEmail.of(validEmail);

    Assertions.assertNotNull(userEmail);
    Assertions.assertEquals(validEmail, userEmail.getEmail());
  }

  @Test
  void testInvalidEmailWithoutAtSymbol() {
    String invalidEmail = "userexample.com";

    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      UserEmail.of(invalidEmail);
    });

    Assertions.assertEquals("Invalid email address: " + invalidEmail, exception.getMessage());
  }

  @Test
  void testInvalidEmailWithoutDomain() {
    String invalidEmail = "user@.com";

    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      UserEmail.of(invalidEmail);
    });

    Assertions.assertEquals("Invalid email address: " + invalidEmail, exception.getMessage());
  }

  @Test
  void testInvalidEmailWithoutTopLevelDomain() {
    String invalidEmail = "user@example";

    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      UserEmail.of(invalidEmail);
    });

    Assertions.assertEquals("Invalid email address: " + invalidEmail, exception.getMessage());
  }

  @Test
  void testNullEmail() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      UserEmail.of(null);
    });

    Assertions.assertEquals("Invalid email address: null", exception.getMessage());
  }

  @Test
  void testEmptyEmail() {
    String invalidEmail = "";

    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      UserEmail.of(invalidEmail);
    });

    Assertions.assertEquals("Invalid email address: ", exception.getMessage());
  }

  @Test
  void testEqualsAndHashCode() {
    UserEmail email1 = UserEmail.of("user@example.com");
    UserEmail email2 = UserEmail.of("user@example.com");
    UserEmail email3 = UserEmail.of("different@example.com");

    Assertions.assertEquals(email1, email2);
    Assertions.assertNotEquals(email1, email3);
    Assertions.assertEquals(email1.hashCode(), email2.hashCode());
    Assertions.assertNotEquals(email1.hashCode(), email3.hashCode());
  }

  @Test
  void testToString() {
    UserEmail userEmail = UserEmail.of("user@example.com");
    Assertions.assertEquals("user@example.com", userEmail.toString());
  }
}
