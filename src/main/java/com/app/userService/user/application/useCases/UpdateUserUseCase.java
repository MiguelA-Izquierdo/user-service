package com.app.userService.user.application.useCases;

import com.app.userService.user.application.bus.command.UpdateUserCommand;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.*;
import com.app.userService.user.domain.valueObjects.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserUseCase {
  private final UserServiceCore userServiceCore;
  private final UserActionLogService userActionLogService;

  public UpdateUserUseCase(UserServiceCore userServiceCore,
                           UserActionLogService userActionLogService){
    this.userServiceCore = userServiceCore;
    this.userActionLogService = userActionLogService;
  }
  @Transactional
  public void execute(UpdateUserCommand updateUserCommand) {
    UserWrapper existingUser = userServiceCore.findUserById(UserId.of(updateUserCommand.userId()));

    if (!existingUser.exists() || !existingUser.isActive()) {
      throw new EntityNotFoundException("User with ID " + updateUserCommand.userId() + " not found");
    }

    User user = existingUser.getUser()
      .orElseThrow(() -> new EntityNotFoundException("User with ID " + updateUserCommand.userId() + " not found"));

    userServiceCore.updateUser(updateUserCommand, user);

    userActionLogService.registerUserAction(user, UserAction.UPDATED);
  }

}
