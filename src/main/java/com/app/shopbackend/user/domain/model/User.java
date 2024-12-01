package com.app.shopbackend.user.domain.model;

import com.app.shopbackend.user.domain.valueObjects.*;

import java.time.LocalDateTime;
import java.util.List;

public class User {
  private final UserId id;
  private final UserName name;
  private final UserLastName lastName;
  private final UserEmail email;
  private final IdentityDocument identityDocument;
  private final Phone phone;
  private final Address address;
  private String password;
  private final LocalDateTime createdAt;
  private final List<Role> roles;

  public User(UserId userId,
              UserName userName,
              UserLastName userLastName,
              UserEmail userEmail,
              IdentityDocument identityDocument,
              Phone phone,
              Address address,
              String password,
              LocalDateTime createdAt,
              List<Role> roles) {
    this.id = userId;
    this.name = userName;
    this.lastName = userLastName;
    this.email = userEmail;
    this.identityDocument = identityDocument;
    this.phone = phone;
    this.address = address;
    this.createdAt = createdAt;
    this.password = password;
    this.roles = roles;
  }

  public UserId getId() {
    return id;
  }

  public UserName getName() {
    return name;
  }

  public UserLastName getLastName() {
    return lastName;
  }

  public UserEmail getEmail() {
    return email;
  }

  public IdentityDocument getIdentityDocument() {
    return identityDocument;
  }

  public Phone getPhone() {
    return phone;
  }

  public Address getAddress() {
    return address;
  }

  public String getPassword() {
    return password;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public void updatePassword(String newPassword){
    this.password = newPassword;
  }
}
