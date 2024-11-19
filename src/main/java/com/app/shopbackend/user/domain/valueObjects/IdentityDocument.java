package com.app.shopbackend.user.domain.valueObjects;

import java.util.Objects;
import java.util.regex.Pattern;

public class IdentityDocument {
  private final String documentType;
  private final String documentNumber;

  private static final Pattern DNI_PATTERN = Pattern.compile("\\d{8}[A-Za-z]");
  private static final Pattern NIE_PATTERN = Pattern.compile("[XYZ]\\d{7}[A-Za-z]");
  private static final Pattern PASSPORT_PATTERN = Pattern.compile("[A-Z0-9]{9}");

  private IdentityDocument(String documentType, String documentNumber) {
    this.documentType = documentType;
    this.documentNumber = documentNumber;
  }

  public static IdentityDocument of(String documentType, String documentNumber) {
    validateDocumentType(documentType);
    validateDocumentNumber(documentType, documentNumber);

    return new IdentityDocument(documentType, documentNumber);
  }
  private static void validateDocumentType(String documentType) {
    Objects.requireNonNull(documentType, "Document type cannot be null");
    if (!documentType.equalsIgnoreCase("DNI") &&
      !documentType.equalsIgnoreCase("NIE") &&
      !documentType.equalsIgnoreCase("Passport")) {
      throw new IllegalArgumentException("Invalid document type. Allowed types: DNI, NIE, Passport");
    }
  }

  // Validación del número de documento
  private static void validateDocumentNumber(String documentType, String documentNumber) {
    Objects.requireNonNull(documentNumber, "Document number cannot be null");

    switch (documentType.toLowerCase()) {
      case "dni" -> {
        if (!DNI_PATTERN.matcher(documentNumber).matches()) {
          throw new IllegalArgumentException("Invalid DNI. It must contain 8 digits followed by a letter.");
        }
      }
      case "nie" -> {
        if (!NIE_PATTERN.matcher(documentNumber).matches()) {
          throw new IllegalArgumentException("Invalid NIE. It must start with X, Y, or Z, followed by 7 digits and a letter.");
        }
      }
      case "passport" -> {
        if (!PASSPORT_PATTERN.matcher(documentNumber).matches()) {
          throw new IllegalArgumentException("Invalid Passport number. It must contain 9 alphanumeric characters.");
        }
      }
      default -> throw new IllegalArgumentException("Unsupported document type.");
    }
  }

  public String getDocumentType() {
    return documentType;
  }

  public String getDocumentNumber() {
    return documentNumber;
  }
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    IdentityDocument that = (IdentityDocument) o;
    return Objects.equals(documentType, that.documentType) &&
      Objects.equals(documentNumber, that.documentNumber);
  }

  @Override
  public int hashCode() {
    return Objects.hash(documentType, documentNumber);
  }

  @Override
  public String toString() {
    return documentType + ": " + documentNumber;
  }
}
