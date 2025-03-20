package com.app.userService.user.domain.valueObjects;

import com.app.userService.user.domain.exceptions.ValueObjectValidationException;

import java.util.HashMap;
import java.util.Map;
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

  public static <T> Map<String, String> getValidationErrors(Map<String, T> args) {
    HashMap<String, String> errors = new HashMap<>();

    String countryCode = (String) args.get("countryCode");
    String number = (String) args.get("number");

    try {
      validateCountryCode(countryCode);
    } catch (ValueObjectValidationException e) {
      errors.put(e.getField(), e.getMessage());
    }

    try {
      validatePhoneNumber(number);
    } catch (ValueObjectValidationException e) {
      errors.put(e.getField(), e.getMessage());
    }

    return errors;
  }
  private static void validateCountryCode(String countryCode) {
    validateNotNullOrEmpty(countryCode, "Country code");
    if (!COUNTRY_CODE_PATTERN.matcher(countryCode).matches()) {
      throw new ValueObjectValidationException("Phone","Invalid country code. It should start with '+' and include 1 to 3 digits.");
    }
  }


  private static void validatePhoneNumber(String number) {
    validateNotNullOrEmpty(number,  "Phone number");
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
  public String toString() {
    return countryCode + "" + number;
  }

  @Override
  protected boolean compareAttributes(Object o) {
    if (!(o instanceof Phone phone)) return false;
    return Objects.equals(countryCode, phone.countryCode) && Objects.equals(number, phone.number);
  }

  @Override
  protected int generateHashCode() {
    return Objects.hash(countryCode, number);
  }
}
