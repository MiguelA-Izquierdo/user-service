package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IdentityDocumentTest {

  @Test
  void testValidDNI() {
    IdentityDocument dni = IdentityDocument.of("DNI", "12345678A");
    assertNotNull(dni);
    assertEquals("DNI", dni.getDocumentType());
    assertEquals("12345678A", dni.getDocumentNumber());
  }

  @Test
  void testValidNIE() {
    IdentityDocument nie = IdentityDocument.of("NIE", "X1234567B");
    assertNotNull(nie);
    assertEquals("NIE", nie.getDocumentType());
    assertEquals("X1234567B", nie.getDocumentNumber());
  }

  @Test
  void testValidPassport() {
    IdentityDocument passport = IdentityDocument.of("Passport", "A12345678");
    assertNotNull(passport);
    assertEquals("Passport", passport.getDocumentType());
    assertEquals("A12345678", passport.getDocumentNumber());
  }

  @Test
  void testInvalidDNI() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of("DNI", "1234567Z")
    );
    assertEquals("Invalid DNI. It must contain 8 digits followed by a letter.", exception.getMessage());
  }

  @Test
  void testInvalidNIE() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of("NIE", "X12345")
    );
    assertEquals("Invalid NIE. It must start with X, Y, or Z, followed by 7 digits and a letter.", exception.getMessage());
  }

  @Test
  void testInvalidPassport() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of("Passport", "12345")
    );
    assertEquals("Invalid Passport number. It must contain 9 alphanumeric characters.", exception.getMessage());
  }

  @Test
  void testUnsupportedDocumentType() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of("UnknownType", "12345678A")
    );
    assertEquals("Invalid document type. Allowed types: DNI, NIE, Passport", exception.getMessage());
  }

  @Test
  void testNullDocumentType() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of(null, "12345678A")
    );
    assertEquals("Document type cannot be null or empty", exception.getMessage());
  }

  @Test
  void testNullDocumentNumber() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of("DNI", null)
    );
    assertEquals("Document number cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEmptyDocumentType() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of("", "12345678A")
    );
    assertEquals("Document type cannot be null or empty", exception.getMessage());
  }

  @Test
  void testEmptyDocumentNumber() {
    Exception exception = assertThrows(ValueObjectValidationException.class, () ->
      IdentityDocument.of("DNI", "")
    );
    assertEquals("Document number cannot be null or empty", exception.getMessage());
  }
}
