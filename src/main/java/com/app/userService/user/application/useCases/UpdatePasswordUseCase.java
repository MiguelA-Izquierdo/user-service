package com.app.userService.user.application.useCases;

import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserPasswordService;
import com.app.userService.user.application.bus.command.UpdatePasswordCommand;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserAction;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.valueObjects.UserId;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class UpdatePasswordUseCase {

  private final UserServiceCore userServiceCore;
  private final UserPasswordService userPasswordService;
  private final UserActionLogService userActionLogService;
  public UpdatePasswordUseCase(UserServiceCore userServiceCore, UserPasswordService userPasswordService, UserActionLogService userActionLogService){
    this.userServiceCore = userServiceCore;
    this.userPasswordService = userPasswordService;
    this.userActionLogService = userActionLogService;
  }
  @Transactional
  public void execute(UpdatePasswordCommand updatePasswordCommand) {
    UserWrapper existingUser = userServiceCore.findUserById(UserId.of(updatePasswordCommand.id()));

    if (!existingUser.exists() || !existingUser.isActive()) {
      throw new EntityNotFoundException("User with ID " + updatePasswordCommand.id() + " not found");
    }

    User user = existingUser.getUser()
      .orElseThrow(() -> new EntityNotFoundException("User with ID " + updatePasswordCommand.id() + " not found"));


    userPasswordService.updatePassword(user, updatePasswordCommand.currentPassword(), updatePasswordCommand.newPassword());

    userActionLogService.registerUserAction(user, UserAction.UPDATE_PASSWORD);
  }
}
