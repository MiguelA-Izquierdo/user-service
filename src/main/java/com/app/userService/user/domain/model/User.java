package com.app.userService.user.domain.model;

import com.app.userService.user.domain.valueObjects.*;

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
  private final UserStatus status;
  private final LocalDateTime createdAt;
  private final List<Role> roles;

  private User(UserId userId,
               UserName userName,
               UserLastName userLastName,
               UserEmail userEmail,
               IdentityDocument identityDocument,
               Phone phone,
               Address address,
               String password,
               LocalDateTime createdAt,
               UserStatus status,
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
    this.status = status;
    this.roles = roles;
  }

  public static User of(UserId userId,
                        UserName userName,
                        UserLastName userLastName,
                        UserEmail userEmail,
                        IdentityDocument identityDocument,
                        Phone phone,
                        Address address,
                        String password,
                        LocalDateTime createdAt,
                        UserStatus status,
                        List<Role> roles) {
    return new Builder()
      .id(userId)
      .name(userName)
      .lastName(userLastName)
      .email(userEmail)
      .identityDocument(identityDocument)
      .phone(phone)
      .address(address)
      .password(password)
      .createdAt(createdAt)
      .status(status)
      .roles(roles)
      .build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UserId id;
    private UserName name;
    private UserLastName lastName;
    private UserEmail email;
    private IdentityDocument identityDocument;
    private Phone phone;
    private Address address;
    private String password;
    private LocalDateTime createdAt;
    private UserStatus status;
    private List<Role> roles;

    public Builder id(UserId id) {
      this.id = id;
      return this;
    }

    public Builder name(UserName name) {
      this.name = name;
      return this;
    }

    public Builder lastName(UserLastName lastName) {
      this.lastName = lastName;
      return this;
    }

    public Builder email(UserEmail email) {
      this.email = email;
      return this;
    }

    public Builder identityDocument(IdentityDocument identityDocument) {
      this.identityDocument = identityDocument;
      return this;
    }

    public Builder phone(Phone phone) {
      this.phone = phone;
      return this;
    }

    public Builder address(Address address) {
      this.address = address;
      return this;
    }

    public Builder password(String password) {
      this.password = password;
      return this;
    }

    public Builder createdAt(LocalDateTime createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public Builder status(UserStatus status) {
      this.status = status;
      return this;
    }

    public Builder roles(List<Role> roles) {
      this.roles = roles;
      return this;
    }

    public User build() {
      return new User(id, name, lastName, email, identityDocument, phone, address, password, createdAt, status, roles);
    }
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

  public UserStatus getStatus() {
    return status;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public void updatePassword(String newPassword) {
    this.password = newPassword;
  }
}
