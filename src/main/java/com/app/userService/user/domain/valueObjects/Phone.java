package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;

import java.util.Objects;
import java.util.regex.Pattern;

public class Phone extends ValueObjectAbstract{
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
    validateNotNullOrEmpty(countryCode, "Country code");
    if (!COUNTRY_CODE_PATTERN.matcher(countryCode).matches()) {
      throw new ValueObjectValidationException("Phone","Invalid country code. It should start with '+' and include 1 to 3 digits.");
    }
  }


  private static void validatePhoneNumber(String number) {
    validateNotNullOrEmpty(number, "Phone number");
    if (!PHONE_NUMBER_PATTERN.matcher(number).matches()) {
      throw new ValueObjectValidationException("Phone","Invalid phone number. It should contain 7 to 10 digits.");
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
