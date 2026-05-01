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
  }

  private static void validateNumber(String number) {
    validateNotNullOrEmpty(number, "Number");
  }

  private static void validateCity(String city) {
    validateNotNullOrEmpty(city, "City");
  }

  private static void validateState(String state) {
    validateNotNullOrEmpty(state,"State");
  }
  private static void validatePostalCode(String postalCode) {
    validateNotNullOrEmpty(postalCode, "Postal code");
  }

  private static void validateCountry(String country) {
    validateNotNullOrEmpty(country, "Country");
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
  public String toString() {
    return street + " " + city + ", " + state + ", " + postalCode + ", " + country;
  }


  @Override
  protected boolean compareAttributes(Object o) {
    if (!(o instanceof Address address)) return false;
    return Objects.equals(street, address.street) &&
      Objects.equals(number, address.number) &&
      Objects.equals(city, address.city) &&
      Objects.equals(state, address.state) &&
      Objects.equals(postalCode, address.postalCode) &&
      Objects.equals(country, address.country);
  }

  @Override
  protected int generateHashCode() {
    return Objects.hash(street, number, city, state, postalCode, country);
  }
}
