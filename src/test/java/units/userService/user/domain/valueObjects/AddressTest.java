package units.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import com.app.userService.user.domain.valueObjects.*;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

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
  void testInvalidValues() {
    assertThrows(ValueObjectValidationException.class, () -> Address.of(null, "123", "Springfield", "IL", "62701", "USA"));
    assertThrows(ValueObjectValidationException.class, () -> Address.of("   ", "123", "Springfield", "IL", "62701", "USA"));
    assertThrows(ValueObjectValidationException.class, () -> Address.of("Main St", null, "Springfield", "IL", "62701", "USA"));
    assertThrows(ValueObjectValidationException.class, () -> Address.of("Main St", "   ", "Springfield", "IL", "62701", "USA"));
    assertThrows(ValueObjectValidationException.class, () -> Address.of("Main St", "123", null, "IL", "62701", "USA"));
    assertThrows(ValueObjectValidationException.class, () -> Address.of("Main St", "123", "   ", "IL", "62701", "USA"));
    assertThrows(ValueObjectValidationException.class, () -> Address.of("Main St", "123", "Springfield", null, "62701", "USA"));
    assertThrows(ValueObjectValidationException.class, () -> Address.of("Main St", "123", "Springfield", "   ", "62701", "USA"));
    assertThrows(ValueObjectValidationException.class, () -> Address.of("Main St", "123", "Springfield", "IL", null, "USA"));
    assertThrows(ValueObjectValidationException.class, () -> Address.of("Main St", "123", "Springfield", "IL", "   ", "USA"));
    assertThrows(ValueObjectValidationException.class, () -> Address.of("Main St", "123", "Springfield", "IL", "62701", null));
    assertThrows(ValueObjectValidationException.class, () -> Address.of("Main St", "123", "Springfield", "IL", "62701", "   "));
  }

  @Test
  void testInvalidPostalCodeFormat() {
    assertThrows(ValueObjectValidationException.class, () -> Address.of("Main St", "123", "Springfield", "IL", "", "USA"));
    assertThrows(ValueObjectValidationException.class, () -> Address.of("Main St", "123", "Springfield", "IL", null, "USA"));
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
  void testGetValidationErrors() {
    Map<String, String> addressMap = new HashMap<>();

    addressMap.put("street", "");
    addressMap.put("streetNumber", null);
    addressMap.put("city", "     ");
    addressMap.put("state", "     ");
    addressMap.put("postalCode", "     ");
    addressMap.put("country", "     ");

    Map<String, String> errors = Address.getValidationErrors(addressMap);

    assertTrue(errors.containsKey("Street"));
    assertTrue(errors.containsKey("Number"));
    assertTrue(errors.containsKey("City"));
    assertTrue(errors.containsKey("State"));
    assertTrue(errors.containsKey("Postal code"));
    assertTrue(errors.containsKey("Country"));

    assertEquals("Street cannot be null or empty", errors.get("Street"));
    assertEquals("Number cannot be null or empty", errors.get("Number"));
    assertEquals("City cannot be null or empty", errors.get("City"));
    assertEquals("State cannot be null or empty", errors.get("State"));
    assertEquals("Postal code cannot be null or empty", errors.get("Postal code"));
    assertEquals("Country cannot be null or empty", errors.get("Country"));
  }
  @Test
  void testGetValidationErrorsIsEmpty() {
    Map<String, String> addressMap = new HashMap<>();

    addressMap.put("street", "Main Street");
    addressMap.put("streetNumber", "123");
    addressMap.put("city", "New York");
    addressMap.put("state", "NY");
    addressMap.put("postalCode", "10001");
    addressMap.put("country", "USA");

    Map<String, String> errors = Address.getValidationErrors(addressMap);

    assertTrue(errors.isEmpty());
  }
}
