package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AddressTest {

  @Test
  void testCreateValidAddress() {
    Address address = Address.of("Main St", "123", "Springfield", "IL", "62701", "USA");

    assertNotNull(address);
    assertEquals("Main St", address.getStreet());
    assertEquals("123", address.getNumber());
    assertEquals("Springfield", address.getCity());
    assertEquals("IL", address.getState());
    assertEquals("62701", address.getPostalCode());
    assertEquals("USA", address.getCountry());
  }

  @Test
  void testStreetNull() {
    assertThrows(ValueObjectValidationException.class, () -> {
      Address.of(null, "123", "Springfield", "IL", "62701", "USA");
    });
  }

  @Test
  void testStreetEmpty() {
    assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("   ", "123", "Springfield", "IL", "62701", "USA");
    });
  }

  @Test
  void testNumberNull() {
    assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", null, "Springfield", "IL", "62701", "USA");
    });
  }

  @Test
  void testNumberEmpty() {
    assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "   ", "Springfield", "IL", "62701", "USA");
    });
  }

  @Test
  void testCityNull() {
    assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", null, "IL", "62701", "USA");
    });
  }

  @Test
  void testCityEmpty() {
    assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", "   ", "IL", "62701", "USA");
    });
  }

  @Test
  void testStateNull() {
    assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", "Springfield", null, "62701", "USA");
    });
  }

  @Test
  void testStateEmpty() {
    assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", "Springfield", "   ", "62701", "USA");
    });
  }

  @Test
  void testPostalCodeNull() {
    assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", "Springfield", "IL", null, "USA");
    });
  }

  @Test
  void testPostalCodeEmpty() {
    assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", "Springfield", "IL", "   ", "USA");
    });
  }

  @Test
  void testCountryNull() {
    assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", "Springfield", "IL", "62701", null);
    });
  }

  @Test
  void testCountryEmpty() {
    assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", "Springfield", "IL", "62701", "   ");
    });
  }

  @Test
  void testEquality() {
    Address address1 = Address.of("Main St", "123", "Springfield", "IL", "62701", "USA");
    Address address2 = Address.of("Main St", "123", "Springfield", "IL", "62701", "USA");
    Address address3 = Address.of("Broadway", "456", "New York", "NY", "10001", "USA");

    assertEquals(address1, address2);
    assertNotEquals(address1, address3);
  }

  @Test
  void testHashCode() {
    Address address1 = Address.of("Main St", "123", "Springfield", "IL", "62701", "USA");
    Address address2 = Address.of("Main St", "123", "Springfield", "IL", "62701", "USA");

    assertEquals(address1.hashCode(), address2.hashCode());
  }

  @Test
  void testToString() {
    Address address = Address.of("Main St", "123", "Springfield", "IL", "62701", "USA");
    String expected = "Main St Springfield, IL, 62701, USA";

    assertEquals(expected, address.toString());
  }

  @Test
  void testAllFieldsNullOrEmpty() {
    assertThrows(ValueObjectValidationException.class, () -> Address.of(null, null, null, null, null, null));
    assertThrows(ValueObjectValidationException.class, () -> Address.of("", "", "", "", "", ""));
  }
}
