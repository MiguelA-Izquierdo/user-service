package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;

import java.util.Objects;

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

  private static void validateStreet(String street) {
    validateNotNullOrEmpty(street, "Street");
    if (street.trim().isEmpty()) {
      throw new ValueObjectValidationException("Address","Street cannot be empty");
    }
  }

  private static void validateNumber(String number) {
    validateNotNullOrEmpty(number, "Number cannot be null");
    if (number.trim().isEmpty()) {
      throw new ValueObjectValidationException("Address","Number cannot be empty");
    }
  }

  private static void validateCity(String city) {
    validateNotNullOrEmpty(city, "City cannot be null");
    if (city.trim().isEmpty()) {
      throw new ValueObjectValidationException("Address","City cannot be empty");
    }
  }

  private static void validateState(String state) {
    validateNotNullOrEmpty(state, "State cannot be null");
    if (state.trim().isEmpty()) {
      throw new ValueObjectValidationException("Address","State cannot be empty");
    }
  }

  private static void validatePostalCode(String postalCode) {
    validateNotNullOrEmpty(postalCode, "Postal code cannot be null");
    if (postalCode.trim().isEmpty()) {
      throw new ValueObjectValidationException("Address","Postal code cannot be empty");
    }
  }

  private static void validateCountry(String country) {
    validateNotNullOrEmpty(country, "Country cannot be null");
    if (country.trim().isEmpty()) {
      throw new ValueObjectValidationException("Address","Country cannot be empty");
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
