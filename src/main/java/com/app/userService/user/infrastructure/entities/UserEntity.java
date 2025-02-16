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

  @Column(name = "secretKey", nullable = false, length = 100)
  private String secretKey;
  @Column(name = "password")
  private String password;
  @Column(name = "created_at")
  private LocalDateTime createdAt;
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus status;
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<UserRoleEntity> roles;
  public UserEntity(){}
  public UserEntity(UUID id,
                      String name,
                      String lastName,
                      String email,
                      String password,
                      String secretKey,
                      UserStatus status,
                      String countryCode,
                      String phoneNumber,
                      String documentType,
                      String documentNumber,
                      String street,
                      String streetNumber,
                      String city,
                      String state,
                      String postalCode,
                      String country,
                    LocalDateTime createdAt) {
    this.id = id;
    this.name = name;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.secretKey = secretKey;
    this.status = status;
    this.countryCode = countryCode;
    this.phoneNumber = phoneNumber;
    this.documentType = documentType;
    this.documentNumber = documentNumber;
    this.street = street;
    this.streetNumber = streetNumber;
    this.city = city;
    this.state = state;
    this.postalCode = postalCode;
    this.country = country;
    this.createdAt = createdAt;
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

