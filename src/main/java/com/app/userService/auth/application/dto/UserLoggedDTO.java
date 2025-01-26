package com.app.userService.auth.application.dto;

import com.app.userService.user.application.dto.UserAuthDTO;

import java.util.Date;
import java.util.List;

public class UserLoggedDTO {

  public record UserDTO(String email, String name, String lastName, List<String> roles) {}

  public record TokenDTO(String token, Date expirationDate) {}

  private final UserDTO user;
  private final TokenDTO token;

  public UserLoggedDTO(UserAuthDTO user, TokenDTO token) {
    this.user = new UserDTO(user.getUserEmail(), user.getUserName(), user.getUserLastName(), user.getRoles());
    this.token = token;
  }

  public static UserLoggedDTO Of(UserAuthDTO userAuthDTO, String token, Date expirationDate) {
    return new UserLoggedDTO(
      userAuthDTO,
      new TokenDTO(token, expirationDate)
    );
  }

  public UserDTO getUser() {
    return user;
  }

  public TokenDTO getToken() {
    return token;
  }
}
