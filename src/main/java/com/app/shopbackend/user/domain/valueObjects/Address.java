package com.app.shopbackend.user.domain.valueObjects;

import java.util.Objects;

public class Address {
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
    Objects.requireNonNull(street, "Street cannot be null");
    if (street.trim().isEmpty()) {
      throw new IllegalArgumentException("Street cannot be empty");
    }
  }

  private static void validateNumber(String number) {
    Objects.requireNonNull(number, "Number cannot be null");
    if (number.trim().isEmpty()) {
      throw new IllegalArgumentException("Number cannot be empty");
    }
  }

  private static void validateCity(String city) {
    Objects.requireNonNull(city, "City cannot be null");
    if (city.trim().isEmpty()) {
      throw new IllegalArgumentException("City cannot be empty");
    }
  }

  private static void validateState(String state) {
    Objects.requireNonNull(state, "State cannot be null");
    if (state.trim().isEmpty()) {
      throw new IllegalArgumentException("State cannot be empty");
    }
  }

  private static void validatePostalCode(String postalCode) {
    Objects.requireNonNull(postalCode, "Postal code cannot be null");
    if (postalCode.trim().isEmpty()) {
      throw new IllegalArgumentException("Postal code cannot be empty");
    }
  }

  private static void validateCountry(String country) {
    Objects.requireNonNull(country, "Country cannot be null");
    if (country.trim().isEmpty()) {
      throw new IllegalArgumentException("Country cannot be empty");
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

  // Override equals and hashCode to ensure value object behavior
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
