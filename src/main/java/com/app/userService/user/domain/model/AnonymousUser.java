package com.app.userService.user.domain.model;


import com.app.userService.user.domain.valueObjects.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AnonymousUser {
  private final UserId id;
  private final String name;
  private final String lastName;
  private final String email;
  private final String documentType;
  private final String documentNumber;
  private final String countryCode;
  private final String number;
  private final String street;
  private final String streetNumber;
  private final String city;
  private final String state;
  private final String postalCode;
  private final String country;
  private final String password;
  private final UserStatus status;
  private final LocalDateTime createdAt;
  private final List<Role> roles;

  public AnonymousUser(UserId userId,
                       String userName,
                       String userLastName,
                       String userEmail,
                       String documentType,
                       String documentNumber,
                       String countryCode,
                       String number,
                       String street,
                       String streetNumber,
                       String city,
                       String state,
                       String postalCode,
                       String country,
                       String password,
                       LocalDateTime createdAt,
                       UserStatus status,
                       List<Role> roles) {
    this.id = userId;
    this.name = anonymizeString(userName);
    this.lastName = anonymizeString(userLastName);
    this.email = anonymizeEmail(userId.getValue());
    this.documentType = anonymizeString(documentType);
    this.documentNumber = anonymizeString(documentNumber);
    this.countryCode = anonymizeString(countryCode);
    this.number = anonymizeString(number);
    this.street = anonymizeString(street);
    this.streetNumber = anonymizeString(streetNumber);
    this.city = anonymizeString(city);
    this.state = anonymizeString(state);
    this.postalCode = anonymizeString(postalCode);
    this.country = anonymizeString(country);
    this.createdAt = createdAt;
    this.password = anonymizeString(password);
    this.status = status;
    this.roles = roles;
  }

  public UserId getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public String getDocumentType() {
    return documentType;
  }

  public String getDocumentNumber() {
    return documentNumber;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public String getNumber() {
    return number;
  }

  public String getStreet() {
    return street;
  }

  public String getStreetNumber() {
    return streetNumber;
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

  public String getPassword() {
    return password;
  }

  public UserStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public List<Role> getRoles() {
    return roles;
  }

  private String anonymizeString(String value) {
    return "*".repeat(value.length());
  }

  public String anonymizeEmail(UUID userId) {
    return "anon-" + userId.toString() + "@anon.com";
  }
}
