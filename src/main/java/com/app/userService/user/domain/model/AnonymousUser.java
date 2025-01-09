package com.app.userService.user.domain.model;

import com.app.userService.user.domain.valueObjects.UserId;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

  public static AnonymousUser of(User user) {
    return new Builder()
      .withId(user.getId())
      .withName(user.getName().getValue())
      .withLastName(user.getLastName().getValue())
      .withEmail(user.getId())
      .withDocumentType(user.getIdentityDocument().getDocumentType())
      .withDocumentNumber(user.getIdentityDocument().getDocumentNumber())
      .withCountryCode(user.getPhone().getCountryCode())
      .withNumber(user.getPhone().getNumber())
      .withStreet(user.getAddress().getStreet())
      .withStreetNumber(user.getAddress().getNumber())
      .withCity(user.getAddress().getCity())
      .withState(user.getAddress().getState())
      .withPostalCode(user.getAddress().getPostalCode())
      .withCountry(user.getAddress().getCountry())
      .withPassword(user.getPassword())
      .withCreatedAt(user.getCreatedAt())
      .withRoles(new ArrayList<>())
      .build();
  }

  private AnonymousUser(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.lastName = builder.lastName;
    this.email = builder.email;
    this.documentType = builder.documentType;
    this.documentNumber = builder.documentNumber;
    this.countryCode = builder.countryCode;
    this.number = builder.number;
    this.street = builder.street;
    this.streetNumber = builder.streetNumber;
    this.city = builder.city;
    this.state = builder.state;
    this.postalCode = builder.postalCode;
    this.country = builder.country;
    this.password = builder.password;
    this.status = builder.status;
    this.createdAt = builder.createdAt;
    this.roles = builder.roles;
  }

  public static class Builder {
    private UserId id;
    private String name;
    private String lastName;
    private String email;
    private String documentType;
    private String documentNumber;
    private String countryCode;
    private String number;
    private String street;
    private String streetNumber;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private String password;
    private UserStatus status = UserStatus.DELETED;
    private LocalDateTime createdAt;
    private List<Role> roles = new ArrayList<>();

    public Builder withId(UserId id) {
      this.id = id;
      return this;
    }

    public Builder withName(String name) {
      this.name = anonymizeString(name);
      return this;
    }

    public Builder withLastName(String lastName) {
      this.lastName = anonymizeString(lastName);
      return this;
    }

    public Builder withEmail(UserId id) {
      this.email = anonymizeEmail(id.getValue());
      return this;
    }

    public Builder withDocumentType(String documentType) {
      this.documentType = anonymizeString(documentType);
      return this;
    }

    public Builder withDocumentNumber(String documentNumber) {
      this.documentNumber = anonymizeString(documentNumber);
      return this;
    }

    public Builder withCountryCode(String countryCode) {
      this.countryCode = anonymizeString(countryCode);
      return this;
    }

    public Builder withNumber(String number) {
      this.number = anonymizeString(number);
      return this;
    }

    public Builder withStreet(String street) {
      this.street = anonymizeString(street);
      return this;
    }

    public Builder withStreetNumber(String streetNumber) {
      this.streetNumber = anonymizeString(streetNumber);
      return this;
    }

    public Builder withCity(String city) {
      this.city = anonymizeString(city);
      return this;
    }

    public Builder withState(String state) {
      this.state = anonymizeString(state);
      return this;
    }

    public Builder withPostalCode(String postalCode) {
      this.postalCode = anonymizeString(postalCode);
      return this;
    }

    public Builder withCountry(String country) {
      this.country = anonymizeString(country);
      return this;
    }

    public Builder withPassword(String password) {
      this.password = anonymizeString(password);
      return this;
    }

    public Builder withCreatedAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder withRoles(List<Role> roles) {
      this.roles = roles;
      return this;
    }

    public AnonymousUser build() {
      return new AnonymousUser(this);
    }

    private String anonymizeString(String value) {
      if (value == null || value.isEmpty()) {
        return "***";
      }
      return "*".repeat(value.length());
    }

    private String anonymizeEmail(UUID userId) {
      return "anon-" + userId.toString() + "@anon.com";
    }
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
}
