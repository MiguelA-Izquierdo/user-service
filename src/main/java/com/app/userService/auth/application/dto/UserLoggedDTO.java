package com.app.userService.auth.application.dto;

import com.app.userService.auth.domain.valueObjects.AuthToken;

import com.app.userService.user.application.dto.UserAuthDTO;

import java.util.Date;
import java.util.List;

public class UserLoggedDTO {
  private final String userEmail;
  private final String token;
  private final Date expirationDate;
  private UserLoggedDTO(String userEmail,
                        String token,
                        Date expirationDate){
    this.userEmail = userEmail;
    this.token = token;
    this.expirationDate = expirationDate;
  }

  public static UserLoggedDTO Of(UserAuthDTO userAuthDTO, AuthToken authToken){
    return new UserLoggedDTO(userAuthDTO.getUserEmail(), authToken.token(), authToken.expirationDate());
  }
  public String getEmail() {
    return userEmail;
  }



  public String getToken() {
    return token;
  }

  public Date getExpirationDate() {
    return expirationDate;
  }
}
