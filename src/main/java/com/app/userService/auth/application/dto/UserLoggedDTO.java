package com.app.userService.auth.application.dto;

import com.app.userService.user.application.dto.UserAuthDTO;
import com.app.userService.user.domain.model.User;

import java.util.List;

public class UserLoggedDTO {

  public record UserDTO(String email, String name, String lastName, List<String> roles) {}


  private final UserDTO user;
  private final String token;

  public UserLoggedDTO(UserAuthDTO user, String token) {
    this.user = new UserDTO(user.getUserEmail(), user.getUserName(), user.getUserLastName(), user.getRoles());
    this.token = token;
  }

  public UserLoggedDTO(User user, String token) {
    this.user = new UserDTO(user.getEmail().toString(), user.getName().getValue(), user.getLastName().getValue(), user.getRoles().stream().map(Enum::name).toList());
    this.token = token;
  }

  public static UserLoggedDTO Of(UserAuthDTO userAuthDTO, String token) {
    return new UserLoggedDTO(
      userAuthDTO,
      token
    );
  }

  public static UserLoggedDTO Of(User user, String token) {
    return new UserLoggedDTO(
      user,
      token
    );
  }

  public UserDTO getUser() {
    return user;
  }

  public String getToken() {
    return token;
  }
}
