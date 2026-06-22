package com.app.userService.user.infrastructure.entities;

import com.app.userService.user.domain.model.UserStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
@Entity
@Table(name = "users")
public class UserEntity {
  @Id
  @Column(name = "id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
  private UUID id;
  @Column(name = "name", nullable = false, length = 100)
  private String name;
  @Column(name = "last_name", nullable = false, length = 100)
  private String lastName;
  @Column(name = "email", unique = true, nullable = false, length = 100)
  private String email;
  @Column(name = "phone_country_code", nullable = false, length = 4)
  private String countryCode;
  @Column(name = "phone_number", nullable = false, length = 15)
  private String phoneNumber;
  @Column(name = "document_type", length = 100)
  private String documentType;
  @Column(name = "document_number", length = 100)
  private String documentNumber;
  @Column(name = "street", nullable = false, length = 500)
  private String street;
  @Column(name = "street_number", length = 500)
  private String streetNumber;
  @Column(name = "city", nullable = false, length = 100)
  private String city;
  @Column(name = "state", nullable = false, length = 100)
  private String state;
  @Column(name = "postal_code", nullable = false, length = 100)
  private String postalCode;
  @Column(name = "country", nullable = false, length = 100)
  private String country;

  @Column(name = "secret_key", nullable = false, length = 100)
  private String secretKey;
  @Column(name = "password")
  private String password;
  @Column(name = "failed_login_attempts")
  private Integer failedLoginAttempts;
  @Column(name = "created_at")
  private LocalDateTime createdAt;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus status;
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<UserRoleEntity> roles;
  public UserEntity(){}

  private UserEntity(Builder builder) {
    this.id = builder.id;
    this.name = builder.name;
    this.lastName = builder.lastName;
    this.email = builder.email;
    this.password = builder.password;
    this.failedLoginAttempts = builder.failedLoginAttempts;
    this.secretKey = builder.secretKey;
    this.status = builder.status;
    this.countryCode = builder.countryCode;
    this.phoneNumber = builder.phoneNumber;
    this.documentType = builder.documentType;
    this.documentNumber = builder.documentNumber;
    this.street = builder.street;
    this.streetNumber = builder.streetNumber;
    this.city = builder.city;
    this.state = builder.state;
    this.postalCode = builder.postalCode;
    this.country = builder.country;
    this.createdAt = builder.createdAt;
  }

  public static Builder builder() { return new Builder(); }

  public static final class Builder {
    private UUID id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private Integer failedLoginAttempts;
    private String secretKey;
    private UserStatus status;
    private String countryCode;
    private String phoneNumber;
    private String documentType;
    private String documentNumber;
    private String street;
    private String streetNumber;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private LocalDateTime createdAt;

    public Builder id(UUID id)                           { this.id = id; return this; }
    public Builder name(String name)                     { this.name = name; return this; }
    public Builder lastName(String lastName)             { this.lastName = lastName; return this; }
    public Builder email(String email)                   { this.email = email; return this; }
    public Builder password(String password)             { this.password = password; return this; }
    public Builder failedLoginAttempts(Integer v)        { this.failedLoginAttempts = v; return this; }
    public Builder secretKey(String secretKey)           { this.secretKey = secretKey; return this; }
    public Builder status(UserStatus status)             { this.status = status; return this; }
    public Builder countryCode(String countryCode)       { this.countryCode = countryCode; return this; }
    public Builder phoneNumber(String phoneNumber)       { this.phoneNumber = phoneNumber; return this; }
    public Builder documentType(String documentType)     { this.documentType = documentType; return this; }
    public Builder documentNumber(String documentNumber) { this.documentNumber = documentNumber; return this; }
    public Builder street(String street)                 { this.street = street; return this; }
    public Builder streetNumber(String streetNumber)     { this.streetNumber = streetNumber; return this; }
    public Builder city(String city)                     { this.city = city; return this; }
    public Builder state(String state)                   { this.state = state; return this; }
    public Builder postalCode(String postalCode)         { this.postalCode = postalCode; return this; }
    public Builder country(String country)               { this.country = country; return this; }
    public Builder createdAt(LocalDateTime createdAt)    { this.createdAt = createdAt; return this; }

    public UserEntity build() { return new UserEntity(this); }
  }


  public UUID getId() {
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

  public String getCountryCode() {
    return countryCode;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getDocumentType() {
    return documentType;
  }

  public String getDocumentNumber() {
    return documentNumber;
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

  public Integer getFailedLoginAttempts() {
    return failedLoginAttempts;
  }
  public String getSecretKey() {
    return secretKey;
  }
  public UserStatus getStatus() {
    return status;
  }

  public List<UserRoleEntity> getRoles() {
    return roles;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}

