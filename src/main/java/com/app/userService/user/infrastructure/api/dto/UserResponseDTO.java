package com.app.userService.user.infrastructure.api.dto;

import com.app.userService.user.domain.model.User;

import java.util.UUID;

public class UserResponseDTO {
  private final UUID id;
  private final String email;
  private final String name;
  private final String lastName;
  private final String phone;
  private final String address;


  private UserResponseDTO(User user){
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }

    this.id = user.getId().getValue();
    this.email = user.getEmail().getEmail();
    this.name = user.getName().getValue();
    this.lastName = user.getLastName().getValue();
    this.phone = user.getPhone().toString();
    this.address = user.getAddress().toString();
  }
  public static UserResponseDTO Of(User user){
    return new UserResponseDTO(user);
  }

  public UUID getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public String getLastName() {
    return lastName;
  }

  public String getPhone() {
    return phone;
  }

  public String getAddress() {
    return address;
  }
}
