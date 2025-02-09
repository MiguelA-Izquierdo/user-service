package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class Address extends ValueObjectAbstract{
  private final String street;
  private final String number;
  private final String city;
  private final String state;
  private final String postalCode;
  private final String country;

  private Address(String street, String number, String city, String state, String postalCode, String country) {
    this.street = street;
    this.number = number;
    this.city = city;
    this.state = state;
    this.postalCode = postalCode;
    this.country = country;
  }

  public static Address of(String street, String number, String city, String state, String postalCode, String country) {
    validateStreet(street);
    validateNumber(number);
    validateCity(city);
    validateState(state);
    validatePostalCode(postalCode);
    validateCountry(country);

    return new Address(street, number, city, state, postalCode, country);
  }
  public static <T> Map<String, String> getValidationErrors(Map<String, T> args) {
    HashMap<String, String> errors = new HashMap<>();

    Map<String, Consumer<Map<String, T>>> validations = Map.of(
      "street", (map) -> validateStreet(getStringValue(map.get("street"))),
      "streetNumber", (map) -> validateNumber(getStringValue(map.get("streetNumber"))),
      "city", (map) -> validateCity(getStringValue(map.get("city"))),
      "state", (map) -> validateState(getStringValue(map.get("state"))),
      "postalCode", (map) -> validatePostalCode(getStringValue(map.get("postalCode"))),
      "country", (map) -> validateCountry(getStringValue(map.get("country")))
    );

    validations.forEach((field, validator) -> {
      try {
        validator.accept(args);
      } catch (ValueObjectValidationException e) {
        errors.put(e.getField(), e.getMessage());
      }
    });

    return errors;
  }

  private static String getStringValue(Object value) {
    return value != null ? value.toString().trim() : "";
  }

  private static void validateStreet(String street) {
    validateNotNullOrEmpty(street, "Street");
    if (street.trim().isEmpty()) {
      throw new ValueObjectValidationException("Street","Street cannot be empty");
    }
  }

  private static void validateNumber(String number) {
    validateNotNullOrEmpty(number, "Number");
    if (number.trim().isEmpty()) {
      throw new ValueObjectValidationException("Number","Number cannot be empty");
    }
  }

  private static void validateCity(String city) {
    validateNotNullOrEmpty(city, "City");
    if (city.trim().isEmpty()) {
      throw new ValueObjectValidationException("City","City cannot be empty");
    }
  }

  private static void validateState(String state) {
    validateNotNullOrEmpty(state,"State");
    if (state.trim().isEmpty()) {
      throw new ValueObjectValidationException("State","State cannot be empty");
    }
  }

  private static void validatePostalCode(String postalCode) {
    validateNotNullOrEmpty(postalCode, "Postal code");
    if (postalCode.trim().isEmpty()) {
      throw new ValueObjectValidationException("Postal code","Postal code cannot be empty");
    }
  }

  private static void validateCountry(String country) {
    validateNotNullOrEmpty(country, "Country");
    if (country.trim().isEmpty()) {
      throw new ValueObjectValidationException("Country","Country cannot be empty");
    }
  }

  public String getStreet() {
    return street;
  }

  public String getNumber() {
    return number;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public String getCountry() {
    return country;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Address address = (Address) o;
    return Objects.equals(street, address.street) &&
      Objects.equals(number, address.number) &&
      Objects.equals(city, address.city) &&
      Objects.equals(state, address.state) &&
      Objects.equals(postalCode, address.postalCode) &&
      Objects.equals(country, address.country);
  }

  @Override
  public int hashCode() {
    return Objects.hash(street, number, city, state, postalCode, country);
  }

  @Override
  public String toString() {
    return (street != null ? street : "") + " "
      + (city != null ? city : "") + ", "
      + (state != null ? state : "") + ", "
      + (postalCode != null ? postalCode : "") + ", "
      + (country != null ? country : "");
  }
}
