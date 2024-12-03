package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PhoneTest {

  @Test
  void testValidPhone() {
    Phone phone = Phone.of("+1", "123456789");
    assertNotNull(phone);
    assertEquals("+1", phone.getCountryCode());
    assertEquals("123456789", phone.getNumber());
    assertEquals("+1123456789", phone.toString());
  }

  @Test
  void testInvalidCountryCode() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      Phone.of("1", "123456789")
    );
    assertEquals("Invalid country code. It should start with '+' and include 1 to 3 digits.", exception.getMessage());
  }

  @Test
  void testEmptyCountryCode() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      Phone.of("", "123456789")
    );
    assertEquals("Country code cannot be null or empty", exception.getMessage());
  }

  @Test
  void testInvalidPhoneNumber() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      Phone.of("+1", "123")
    );
    assertEquals("Invalid phone number. It should contain 7 to 10 digits.", exception.getMessage());
  }

  @Test
  void testEmptyPhoneNumber() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      Phone.of("+1", "")
    );
    assertEquals("Phone number cannot be null or empty", exception.getMessage());
  }

  @Test
  void testNullCountryCode() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      Phone.of(null, "123456789")
    );
    assertEquals("Country code cannot be null or empty", exception.getMessage());
  }

  @Test
  void testNullPhoneNumber() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      Phone.of("+1", null)
    );
    assertEquals("Phone number cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEqualsAndHashCode() {
    Phone phone1 = Phone.of("+1", "123456789");
    Phone phone2 = Phone.of("+1", "123456789");
    Phone phone3 = Phone.of("+44", "987654321");

    assertEquals(phone1, phone2);
    assertNotEquals(phone1, phone3);
    assertEquals(phone1.hashCode(), phone2.hashCode());
    assertNotEquals(phone1.hashCode(), phone3.hashCode());
  }

  @Test
  void testToString() {
    Phone phone = Phone.of("+44", "987654321");
    assertEquals("+44987654321", phone.toString());
  }
}
