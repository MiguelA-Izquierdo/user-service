package com.app.userService.auth.application.useCases;


import com.app.userService._shared.application.service.UserEventService;
import com.app.userService.auth.application.bus.query.LoginQuery;
import com.app.userService.auth.application.dto.UserLoggedDTO;
import com.app.userService.auth.application.service.LoginService;

import com.app.userService.auth.domain.service.AuthService;
import com.app.userService.auth.domain.valueObjects.AuthToken;

import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserServiceCore;

import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserAction;
import com.app.userService.user.domain.model.UserWrapper;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;


@Service
public class LoginUseCase {
  private final UserServiceCore userServiceCore;
  private final UserActionLogService userActionLogService;
  private final AuthService authService;
  private final LoginService loginService;
  private final UserEventService userEventService;
  public LoginUseCase(UserServiceCore userServiceCore,
                      AuthService authService,
                      UserActionLogService userActionLogService,
                      UserEventService userEventService,
                      LoginService loginService){
    this.userServiceCore = userServiceCore;
    this.authService = authService;
    this.userActionLogService = userActionLogService;
    this.loginService = loginService;
    this.userEventService = userEventService;
  }
  public UserLoggedDTO execute(LoginQuery loginQuery) {
    UserWrapper existingUser = this.userServiceCore.findUserByEmail(loginQuery.email());
    if (!existingUser.exists() || !existingUser.isActive()) {
      throw new EntityNotFoundException("User with ID "  + " not found");
    }

    User user = existingUser.getUser()
      .orElseThrow(() -> new EntityNotFoundException("User with ID " + " not found"));

    this.loginService.login(user, loginQuery.password());

    this.userActionLogService.registerUserAction(user, UserAction.LOGGED);

    userEventService.handleUserLoggedEvent(user, false);

    AuthToken authToken = this.authService.generateToken(user);

    return UserLoggedDTO.Of(user, authToken.token());
  }
}
