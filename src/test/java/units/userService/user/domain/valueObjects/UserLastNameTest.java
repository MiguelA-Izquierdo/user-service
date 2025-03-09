package units.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import com.app.userService.user.domain.valueObjects.UserLastName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class UserLastNameTest {

  @Test
  void testValidUserLastName() {
    UserLastName userLastName = UserLastName.of("Smith");
    Assertions.assertNotNull(userLastName);
    Assertions.assertEquals("Smith", userLastName.getValue());
  }

  @Test
  void testMinimumLengthUserLastName() {
    UserLastName userLastName = UserLastName.of("Lee");
    Assertions.assertNotNull(userLastName);
    Assertions.assertEquals("Lee", userLastName.getValue());
  }

  @Test
  void testMaximumLengthUserLastName() {
    String longLastName = "a".repeat(100);
    UserLastName userLastName = UserLastName.of(longLastName);
    Assertions.assertNotNull(userLastName);
    Assertions.assertEquals(longLastName, userLastName.getValue());
  }

  @Test
  void testUserLastNameTooShort() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      UserLastName.of("Al")
    );
    Assertions.assertEquals("The User last name must be between 3 and 100 characters long.", exception.getMessage());
  }

  @Test
  void testUserLastNameTooLong() {
    String tooLongLastName = "a".repeat(101);
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      UserLastName.of(tooLongLastName)
    );
    Assertions.assertEquals("The User last name must be between 3 and 100 characters long.", exception.getMessage());
  }

  @Test
  void testNullUserLastName() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      UserLastName.of(null)
    );
    Assertions.assertEquals("User last cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEmptyUserLastName() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      UserLastName.of("")
    );
    Assertions.assertEquals("User last cannot be null or empty", exception.getMessage());
  }

  @Test
  void testWhitespaceUserLastName() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      UserLastName.of("   ")
    );
    Assertions.assertEquals("User last cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEqualsAndHashCode() {
    UserLastName lastName1 = UserLastName.of("Johnson");
    UserLastName lastName2 = UserLastName.of("Johnson");
    UserLastName lastName3 = UserLastName.of("Doe");

    Assertions.assertEquals(lastName1, lastName2);
    Assertions.assertNotEquals(lastName1, lastName3);
    Assertions.assertEquals(lastName1.hashCode(), lastName2.hashCode());
    Assertions.assertNotEquals(lastName1.hashCode(), lastName3.hashCode());
  }

  @Test
  void testToString() {
    UserLastName userLastName = UserLastName.of("Brown");
    Assertions.assertEquals("Brown", userLastName.toString());
  }
}
