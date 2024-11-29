package com.app.shopbackend.user.infraestructure.api.dto;

import com.app.shopbackend.user.domain.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserResponseDTO {
  private final String userEmail;
  private final List<String> roles;

  private UserResponseDTO(User user){
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }

    this.userEmail = user.getEmail().getEmail();
    this.roles = user.getRoles().stream()
      .map(Enum::name)
      .collect(Collectors.toList());
  }
  public static UserResponseDTO Of(User user){
    return new UserResponseDTO(user);
  }

  public String getUserEmail() {
    return userEmail;
  }

  public List<String> getRoles() {
    return roles;
  }
}
