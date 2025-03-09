package units.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import com.app.userService.user.domain.valueObjects.Phone;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class PhoneTest {

  @Test
  void testValidPhone() {
    Phone phone = Phone.of("+1", "123456789");
    Assertions.assertNotNull(phone);
    Assertions.assertEquals("+1", phone.getCountryCode());
    Assertions.assertEquals("123456789", phone.getNumber());
    Assertions.assertEquals("+1123456789", phone.toString());
  }

  @Test
  void testInvalidCountryCode() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      Phone.of("1", "123456789")
    );
    Assertions.assertEquals("Invalid country code. It should start with '+' and include 1 to 3 digits.", exception.getMessage());
  }

  @Test
  void testEmptyCountryCode() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      Phone.of("", "123456789")
    );
    Assertions.assertEquals("Country code cannot be null or empty", exception.getMessage());
  }

  @Test
  void testInvalidPhoneNumber() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      Phone.of("+1", "123")
    );
    Assertions.assertEquals("Invalid phone number. It should contain 7 to 10 digits.", exception.getMessage());
  }

  @Test
  void testEmptyPhoneNumber() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      Phone.of("+1", "")
    );
    Assertions.assertEquals("Phone number cannot be null or empty", exception.getMessage());
  }

  @Test
  void testNullCountryCode() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      Phone.of(null, "123456789")
    );
    Assertions.assertEquals("Country code cannot be null or empty", exception.getMessage());
  }

  @Test
  void testNullPhoneNumber() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      Phone.of("+1", null)
    );
    Assertions.assertEquals("Phone number cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEqualsAndHashCode() {
    Phone phone1 = Phone.of("+1", "123456789");
    Phone phone2 = Phone.of("+1", "123456789");
    Phone phone3 = Phone.of("+44", "987654321");

    Assertions.assertEquals(phone1, phone2);
    Assertions.assertNotEquals(phone1, phone3);
    Assertions.assertEquals(phone1.hashCode(), phone2.hashCode());
    Assertions.assertNotEquals(phone1.hashCode(), phone3.hashCode());
  }

  @Test
  void testToString() {
    Phone phone = Phone.of("+44", "987654321");
    Assertions.assertEquals("+44987654321", phone.toString());
  }
}
