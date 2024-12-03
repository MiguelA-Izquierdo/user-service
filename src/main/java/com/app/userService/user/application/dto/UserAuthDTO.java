package com.app.userService.user.application.dto;

import com.app.userService.user.domain.model.User;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserAuthDTO {
  private final UUID id;
  private final String userEmail;
  private final String password;
  private final List<String> roles;

  private UserAuthDTO(User user){
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }

    this.id = user.getId().getValue();
    this.userEmail = user.getEmail().getEmail();
    this.password = user.getPassword();
    this.roles = user.getRoles().stream()
      .map(Enum::name)
      .collect(Collectors.toList());
  }
  public static UserAuthDTO Of(User user){
    return new UserAuthDTO(user);
  }

  public UUID getId() {
    return id;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public String getPassword() {
    return password;
  }

  public List<String> getRoles() {
    return roles;
  }
}
