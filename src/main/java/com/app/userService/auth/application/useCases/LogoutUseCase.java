package com.app.userService.auth.application.useCases;


import com.app.userService._shared.application.service.UserEventService;
import com.app.userService.auth.application.bus.command.LogoutUserCommand;
import com.app.userService.auth.application.service.LoginService;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserAction;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.valueObjects.UserId;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class LogoutUseCase {
  private final UserServiceCore userServiceCore;
  private final UserActionLogService userActionLogService;
  private final LoginService loginService;
  private final UserEventService userEventService;

  public LogoutUseCase(UserServiceCore userServiceCore,
                       UserActionLogService userActionLogService,
                       UserEventService userEventService,
                       LoginService loginService ){
    this.userServiceCore = userServiceCore;
    this.userActionLogService = userActionLogService;
    this.loginService = loginService;
    this.userEventService = userEventService;
  }
  @Transactional
  public void execute(LogoutUserCommand command) {
    UserWrapper existingUser = this.userServiceCore.findUserById(UserId.of(command.userId()));
    if (!existingUser.exists() || !existingUser.isActive()) {
      throw new EntityNotFoundException("User with ID "  + " not found");
    }
    User user = existingUser.getUser()
      .orElseThrow(() -> new EntityNotFoundException("User with ID " + " not found"));

    loginService.logoutUser(user);

    userEventService.handleUserLogoutEvent(user);

    userActionLogService.addMetadata("executorUserId", command.executorUserId());
    userActionLogService.registerUserAction(user, UserAction.LOGOUT);
  }

}
