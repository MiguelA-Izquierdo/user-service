package com.app.shopbackend.user.domain.valueObjects;

import java.util.Objects;
import java.util.regex.Pattern;

public class Phone {
  private final String countryCode;
  private final String number;

  private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("\\d{7,10}");
  private static final Pattern COUNTRY_CODE_PATTERN = Pattern.compile("\\+\\d{1,3}");

  private Phone(String countryCode, String number) {
    this.countryCode = countryCode;
    this.number = number;
  }

  public static Phone of(String countryCode, String number) {
    validateCountryCode(countryCode);
    validatePhoneNumber(number);

    return new Phone(countryCode, number);
  }

  private static void validateCountryCode(String countryCode) {
    Objects.requireNonNull(countryCode, "Country code cannot be null");
    if (!COUNTRY_CODE_PATTERN.matcher(countryCode).matches()) {
      throw new IllegalArgumentException("Invalid country code. It should start with '+' and include 1 to 3 digits.");
    }
  }


  private static void validatePhoneNumber(String number) {
    Objects.requireNonNull(number, "Phone number cannot be null");
    if (!PHONE_NUMBER_PATTERN.matcher(number).matches()) {
      throw new IllegalArgumentException("Invalid phone number. It should contain 7 to 10 digits.");
    }
  }

  public String getCountryCode() {
    return countryCode;
  }


  public String getNumber() {
    return number;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Phone phone = (Phone) o;
    return Objects.equals(countryCode, phone.countryCode) &&
      Objects.equals(number, phone.number);
  }

  @Override
  public int hashCode() {
    return Objects.hash(countryCode, number);
  }

  @Override
  public String toString() {
    return countryCode + "" + number;
  }
}
