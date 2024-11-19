package com.app.shopbackend.user.domain.model;

import com.app.shopbackend.user.domain.valueObjects.*;

public class User {
  private final UserId id;
  private final UserName name;
  private final UserLastName lastName;
  private final UserEmail email;
  private final IdentityDocument identityDocument;
  private final Phone phone;
  private final Address address;

  public User(UserId userId,
              UserName userName,
              UserLastName userLastName,
              UserEmail userEmail,
              IdentityDocument identityDocument,
              Phone phone,
              Address address) {
    this.id = userId;
    this.name = userName;
    this.lastName = userLastName;
    this.email = userEmail;
    this.identityDocument = identityDocument;
    this.phone = phone;
    this.address = address;
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
}
