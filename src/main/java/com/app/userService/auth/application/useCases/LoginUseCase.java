package com.app.userService.auth.application.useCases;


import com.app.userService._shared.exceptions.InvalidPasswordException;
import com.app.userService.auth.application.bus.query.LoginQuery;
import com.app.userService.auth.application.dto.UserLoggedDTO;

import com.app.userService.auth.domain.service.AuthService;
import com.app.userService.auth.domain.valueObjects.AuthToken;


import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserServiceCore;

import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserWrapper;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.util.HashMap;
import java.util.Map;


@Service
public class LoginUseCase {
  private final UserServiceCore userServiceCore;
  private final UserActionLogService userActionLogService;
  private final AuthService authService;
  public LoginUseCase(UserServiceCore userServiceCore, AuthService authService, UserActionLogService userActionLogService){
    this.userServiceCore = userServiceCore;
    this.authService = authService;
    this.userActionLogService = userActionLogService;
  }

  public UserLoggedDTO execute(LoginQuery loginQuery) {
    UserWrapper existingUser = this.userServiceCore.findUserByEmail(loginQuery.email());
    if (!existingUser.exists() || !existingUser.isActive()) {
      throw new EntityNotFoundException("User with ID "  + " not found");
    }
    User user = existingUser.getUser()
      .orElseThrow(() -> new EntityNotFoundException("User with ID " + " not found"));

    boolean isCredentialsValid = this.userServiceCore.verifyPassword(loginQuery.password(), user.getPassword());

    if(isCredentialsValid){
      //Temporary until we create the query to save and obtain the token from the cache
      Map<String, String> metaData = new HashMap<>();
      this.userActionLogService.registerLoginWithoutToken(user, metaData) ;

      AuthToken authToken = this.authService.generateToken(user);

      return UserLoggedDTO.Of(user, authToken.token());
    }else{
      throw new InvalidPasswordException("The current password provided is incorrect.");
    }

  }
}
