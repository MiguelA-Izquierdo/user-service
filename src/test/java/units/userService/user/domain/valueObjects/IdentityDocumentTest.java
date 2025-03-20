package units.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import com.app.userService.user.domain.valueObjects.IdentityDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


class IdentityDocumentTest {

  @Test
  void testValidDNI() {
    IdentityDocument dni = IdentityDocument.of("DNI", "12345678A");
    Assertions.assertNotNull(dni);
    Assertions.assertEquals("DNI", dni.getDocumentType());
    Assertions.assertEquals("12345678A", dni.getDocumentNumber());
  }

  @Test
  void testValidNIE() {
    IdentityDocument nie = IdentityDocument.of("NIE", "X1234567B");
    Assertions.assertNotNull(nie);
    Assertions.assertEquals("NIE", nie.getDocumentType());
    Assertions.assertEquals("X1234567B", nie.getDocumentNumber());
  }

  @Test
  void testValidPassport() {
    IdentityDocument passport = IdentityDocument.of("Passport", "A12345678");
    Assertions.assertNotNull(passport);
    Assertions.assertEquals("Passport", passport.getDocumentType());
    Assertions.assertEquals("A12345678", passport.getDocumentNumber());
  }

  @Test
  void testInvalidDNI() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of("DNI", "1234567Z")
    );
    Assertions.assertEquals("Invalid DNI. It must contain 8 digits followed by a letter.", exception.getMessage());
  }

  @Test
  void testInvalidNIE() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of("NIE", "X12345")
    );
    Assertions.assertEquals("Invalid NIE. It must start with X, Y, or Z, followed by 7 digits and a letter.", exception.getMessage());
  }

  @Test
  void testInvalidPassport() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of("Passport", "12345")
    );
    Assertions.assertEquals("Invalid Passport number. It must contain 9 alphanumeric characters.", exception.getMessage());
  }

  @Test
  void testUnsupportedDocumentType() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of("UnknownType", "12345678A")
    );
    Assertions.assertEquals("Invalid document type. Allowed types: DNI, NIE, Passport", exception.getMessage());
  }

  @Test
  void testNullDocumentType() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of(null, "12345678A")
    );
    Assertions.assertEquals("Document type cannot be null or empty", exception.getMessage());
  }

  @Test
  void testNotValidDocumentType() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of("LOL", "12345678A")
    );
    Assertions.assertEquals("Invalid document type. Allowed types: DNI, NIE, Passport", exception.getMessage());
  }

  @Test
  void testNullDocumentNumber() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of("DNI", null)
    );
    Assertions.assertEquals("Document number cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEmptyDocumentType() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of("", "12345678A")
    );
    Assertions.assertEquals("Document type cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEmptyDocumentNumber() {
    Exception exception = Assertions.assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of("DNI", "")
    );
    Assertions.assertEquals("Document number cannot be null or empty", exception.getMessage());
  }

  @Test
  void testGetValidationErrors() {
    Map<String, String> identityDocumentMap = new HashMap<>();

    identityDocumentMap.put("documentType", "");
    identityDocumentMap.put("documentNumber", null);

    Map<String, String> errors = IdentityDocument.getValidationErrors(identityDocumentMap);

    assertEquals("Document type cannot be null or empty", errors.get("Document type"));
    assertEquals("Document number cannot be null or empty", errors.get("Document number"));
  }
  @Test
  void testGetValidationErrorsIsEmpty() {
    Map<String, String> identityDocumentMap = new HashMap<>();

    identityDocumentMap.put("documentType", "DNI");
    identityDocumentMap.put("documentNumber", "12345678A");

    Map<String, String> errors = IdentityDocument.getValidationErrors(identityDocumentMap);

    assertTrue(errors.isEmpty());
  }

  @Test
  void testEquals_SameObject() {
    IdentityDocument doc1 = IdentityDocument.of("Passport", "A12345678");
    assertEquals(doc1, doc1, "Un objeto debe ser igual a sí mismo");
  }

  @Test
  void testEquals_NullObject() {
    IdentityDocument doc1 = IdentityDocument.of("Passport", "A12345678");
    assertNotNull(doc1, "Un objeto no debe ser igual a null");
  }
  @Test
  void testEquals_EqualObjects() {
    IdentityDocument doc1 = IdentityDocument.of("Passport", "A12345678");
    IdentityDocument doc2 = IdentityDocument.of("Passport", "A12345678");
    assertEquals(doc1, doc2, "Dos objetos con el mismo tipo y número deben ser iguales");
  }

  @Test
  void testEquals_DifferentDocumentType() {
    IdentityDocument doc1 = IdentityDocument.of("Passport", "A12345678");
    IdentityDocument doc2 = IdentityDocument.of("DNI", "12345678A");
    assertNotEquals(doc1, doc2, "Objetos con diferente tipo de documento no deben ser iguales");
  }

  @Test
  void testEquals_DifferentDocumentNumber() {
    IdentityDocument doc1 = IdentityDocument.of("Passport", "A12345678");
    IdentityDocument doc2 = IdentityDocument.of("Passport", "E12345678");
    assertNotEquals(doc1, doc2, "Objetos con diferente número de documento no deben ser iguales");
  }
}
