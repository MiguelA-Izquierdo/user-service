package com.app.userService.auth.application.useCases;


import com.app.userService.auth.application.bus.command.LogoutUserCommand;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.User;
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
  public LogoutUseCase(UserServiceCore userServiceCore,  UserActionLogService userActionLogService){
    this.userServiceCore = userServiceCore;
    this.userActionLogService = userActionLogService;
  }
  @Transactional
  public void execute(LogoutUserCommand command) {
    UserWrapper existingUser = this.userServiceCore.findUserById(UserId.of(command.userId()));
    if (!existingUser.exists() || !existingUser.isActive()) {
      throw new EntityNotFoundException("User with ID "  + " not found");
    }
    User user = existingUser.getUser()
      .orElseThrow(() -> new EntityNotFoundException("User with ID " + " not found"));

    Map<String, String> metaData = new HashMap<>();
    metaData.put("executorUserId", command.executorUserId());

    userServiceCore.logoutUser(user);
    userActionLogService.registerLogout(user, metaData);
    }

}
