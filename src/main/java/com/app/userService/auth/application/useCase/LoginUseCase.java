package com.app.userService.auth.application.useCase;

import com.app.userService._shared.exceptions.InvalidPasswordException;
import com.app.userService.auth.application.bus.query.LoginQuery;
import com.app.userService.auth.application.dto.UserLoggedDTO;
import com.app.userService.auth.domain.service.AuthService;
import com.app.userService.auth.domain.valueObjects.AuthToken;


import com.app.userService.user.application.dto.UserAuthDTO;
import com.app.userService.user.application.service.UserServiceCore;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;


@Service
public class LoginUseCase {
  private final UserServiceCore userServiceCore;
  private final AuthService authService;
  public LoginUseCase(UserServiceCore userServiceCore, AuthService authService){
    this.userServiceCore = userServiceCore;
    this.authService = authService;

  }

  public UserLoggedDTO execute(LoginQuery loginQuery) {
    UserAuthDTO user = this.userServiceCore.findUserByEmail(loginQuery.email())
      .orElseThrow(() -> new EntityNotFoundException("User not found: " + loginQuery.email()));
    boolean isCredentialsValid = this.userServiceCore.verifyPassword(loginQuery.password(), user.getPassword());

    if(isCredentialsValid){
      AuthToken authToken = this.authService.generateToken(user.getId().toString(), user.getRoles());
      return UserLoggedDTO.Of(user, authToken);
    }else{
      throw new InvalidPasswordException("The current password provided is incorrect.");
    }

  }
}
