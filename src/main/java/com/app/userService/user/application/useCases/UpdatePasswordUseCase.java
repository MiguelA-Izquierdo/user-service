package com.app.userService.user.application.useCases;

import com.app.userService.user.application.bus.command.UpdatePasswordCommand;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.valueObjects.UserId;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class UpdatePasswordUseCase {

  private final UserServiceCore userServiceCore;
  public UpdatePasswordUseCase(UserServiceCore userServiceCore){
    this.userServiceCore = userServiceCore;
  }
  @Transactional
  public void execute(UpdatePasswordCommand updatePasswordCommand) {
    UserWrapper existingUser = userServiceCore.findUserById(UserId.of(updatePasswordCommand.id()));

    if (!existingUser.exists() || !existingUser.isActive()) {
      throw new EntityNotFoundException("User with ID " + updatePasswordCommand.id() + " not found");
    }

    User user = existingUser.getUser()
      .orElseThrow(() -> new EntityNotFoundException("User with ID " + updatePasswordCommand.id() + " not found"));


    userServiceCore.updatePassword(user, updatePasswordCommand.currentPassword(), updatePasswordCommand.newPassword());
  }
}
