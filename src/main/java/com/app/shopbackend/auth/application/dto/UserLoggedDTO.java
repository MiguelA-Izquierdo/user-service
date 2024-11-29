package com.app.shopbackend.auth.application.dto;

import com.app.shopbackend.auth.domain.valueObjects.AuthToken;

import com.app.shopbackend.user.application.dto.UserAuthDTO;

import java.util.Date;
import java.util.List;

public class UserLoggedDTO {
  private final String userEmail;
  private final List<String> roles;
  private final String token;
  private final Date expirationDate;
  private UserLoggedDTO(String userEmail,
                        List<String> roles,
                        String token,
                        Date expirationDate){
    this.userEmail = userEmail;
    this.roles = roles;
    this.token = token;
    this.expirationDate = expirationDate;
  }

  public static UserLoggedDTO Of(UserAuthDTO userAuthDTO, AuthToken authToken){
    return new UserLoggedDTO(userAuthDTO.getUserEmail(), userAuthDTO.getRoles(), authToken.token(), authToken.expirationDate());
  }
  public String getUserEmail() {
    return userEmail;
  }

  public List<String> getRoles() {
    return roles;
  }

  public String getToken() {
    return token;
  }

  public Date getExpirationDate() {
    return expirationDate;
  }
}
