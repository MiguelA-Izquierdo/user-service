package com.app.shopbackend.auth.application.useCase;

import com.app.shopbackend._shared.exceptions.InvalidPasswordException;
import com.app.shopbackend.auth.application.bus.query.LoginQuery;
import com.app.shopbackend.auth.application.dto.UserLoggedDTO;
import com.app.shopbackend.auth.domain.service.AuthService;
import com.app.shopbackend.auth.domain.valueObjects.AuthToken;


import com.app.shopbackend.user.application.dto.UserAuthDTO;
import com.app.shopbackend.user.application.service.UserService;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;


@Service
public class LoginUseCase {
  private final UserService userService;
  private final AuthService authService;
  public LoginUseCase(UserService userService, AuthService authService){
    this.userService = userService;
    this.authService = authService;

  }

  public UserLoggedDTO execute(LoginQuery loginQuery) {
    UserAuthDTO user = this.userService.findUserByEmail(loginQuery.email())
      .orElseThrow(() -> new EntityNotFoundException("User not found: " + loginQuery.email()));
    boolean isCredentialsValid = this.userService.verifyPassword(loginQuery.password(), user.getPassword());

    if(isCredentialsValid){
      AuthToken authToken = this.authService.generateToken(user.getId().toString(), user.getRoles());
      return UserLoggedDTO.Of(user, authToken);
    }else{
      throw new InvalidPasswordException("The current password provided is incorrect.");
    }

  }
}
