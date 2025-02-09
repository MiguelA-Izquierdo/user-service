package com.app.userService.auth.application.useCases;


import com.app.userService.auth.application.bus.query.LoginWithTokenQuery;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.auth.application.dto.UserLoggedDTO;

import com.app.userService.auth.domain.service.AuthService;
import com.app.userService.auth.domain.valueObjects.AuthToken;

import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.valueObjects.UserId;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class LoginWithTokenUseCase {
  private final UserServiceCore userServiceCore;
  private final AuthService authService;
  private final UserActionLogService userActionLogService;
  public LoginWithTokenUseCase(UserServiceCore userServiceCore, AuthService authService, UserActionLogService userActionLogService){
    this.userServiceCore = userServiceCore;
    this.authService = authService;
    this.userActionLogService = userActionLogService;
  }

  public UserLoggedDTO execute(LoginWithTokenQuery loginQuery) {
    UserWrapper existingUser = this.userServiceCore.findUserById(UserId.of(loginQuery.userId()));

    if (!existingUser.exists() || !existingUser.isActive()) {
      throw new EntityNotFoundException("User with ID "  + " not found");
    }

    User user = existingUser.getUser()
      .orElseThrow(() -> new EntityNotFoundException("User with ID " + " not found"));

    //Temporary until we create the query to save and obtain the token from the cache
    Map<String, String> metaData = new HashMap<>();
    this.userActionLogService.registerLoginWithToken(user, metaData); ;

      AuthToken authToken = this.authService.generateToken(user);
      return UserLoggedDTO.Of(user, authToken.token());
  }
}
