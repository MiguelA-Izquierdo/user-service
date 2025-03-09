package units.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import com.app.userService.user.domain.valueObjects.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AddressTest {

  @Test
  void testCreateValidAddress() {
    Address address = Address.of("Main St", "123", "Springfield", "IL", "62701", "USA");

    Assertions.assertNotNull(address);
    Assertions.assertEquals("Main St", address.getStreet());
    Assertions.assertEquals("123", address.getNumber());
    Assertions.assertEquals("Springfield", address.getCity());
    Assertions.assertEquals("IL", address.getState());
    Assertions.assertEquals("62701", address.getPostalCode());
    Assertions.assertEquals("USA", address.getCountry());
  }

  @Test
  void testStreetNull() {
    Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      Address.of(null, "123", "Springfield", "IL", "62701", "USA");
    });
  }

  @Test
  void testStreetEmpty() {
    Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("   ", "123", "Springfield", "IL", "62701", "USA");
    });
  }

  @Test
  void testNumberNull() {
    Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", null, "Springfield", "IL", "62701", "USA");
    });
  }

  @Test
  void testNumberEmpty() {
    Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "   ", "Springfield", "IL", "62701", "USA");
    });
  }

  @Test
  void testCityNull() {
    Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", null, "IL", "62701", "USA");
    });
  }

  @Test
  void testCityEmpty() {
    Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", "   ", "IL", "62701", "USA");
    });
  }

  @Test
  void testStateNull() {
    Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", "Springfield", null, "62701", "USA");
    });
  }

  @Test
  void testStateEmpty() {
    Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", "Springfield", "   ", "62701", "USA");
    });
  }

  @Test
  void testPostalCodeNull() {
    Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", "Springfield", "IL", null, "USA");
    });
  }

  @Test
  void testPostalCodeEmpty() {
    Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", "Springfield", "IL", "   ", "USA");
    });
  }

  @Test
  void testCountryNull() {
    Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", "Springfield", "IL", "62701", null);
    });
  }

  @Test
  void testCountryEmpty() {
    Assertions.assertThrows(ValueObjectValidationException.class, () -> {
      Address.of("Main St", "123", "Springfield", "IL", "62701", "   ");
    });
  }

  @Test
  void testEquality() {
    Address address1 = Address.of("Main St", "123", "Springfield", "IL", "62701", "USA");
    Address address2 = Address.of("Main St", "123", "Springfield", "IL", "62701", "USA");
    Address address3 = Address.of("Broadway", "456", "New York", "NY", "10001", "USA");

    Assertions.assertEquals(address1, address2);
    Assertions.assertNotEquals(address1, address3);
  }

  @Test
  void testHashCode() {
    Address address1 = Address.of("Main St", "123", "Springfield", "IL", "62701", "USA");
    Address address2 = Address.of("Main St", "123", "Springfield", "IL", "62701", "USA");

    Assertions.assertEquals(address1.hashCode(), address2.hashCode());
  }

  @Test
  void testToString() {
    Address address = Address.of("Main St", "123", "Springfield", "IL", "62701", "USA");
    String expected = "Main St Springfield, IL, 62701, USA";

    Assertions.assertEquals(expected, address.toString());
  }

  @Test
  void testAllFieldsNullOrEmpty() {
    Assertions.assertThrows(ValueObjectValidationException.class, () -> Address.of(null, null, null, null, null, null));
    Assertions.assertThrows(ValueObjectValidationException.class, () -> Address.of("", "", "", "", "", ""));
  }
}
