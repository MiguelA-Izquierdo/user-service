package com.app.userService.user.application.useCases;

import com.app.userService.user.application.bus.command.GrantSuperAdminCommand;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.valueObjects.UserId;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
public class GrantSuperAdminUseCase {

  private final UserServiceCore userServiceCore;
  public GrantSuperAdminUseCase(UserServiceCore userServiceCore){
    this.userServiceCore = userServiceCore;
  }
  @Transactional
  public void execute(GrantSuperAdminCommand grantSuperAdminCommand) {
    UserWrapper existingUser = userServiceCore.findUserById(UserId.of(grantSuperAdminCommand.id()));

    if (!existingUser.exists() || !existingUser.isActive()) {
      throw new EntityNotFoundException("User with ID " + grantSuperAdminCommand.id() + " not found");
    }

    User user = existingUser.getUser()
      .orElseThrow(() -> new EntityNotFoundException("User with ID " + grantSuperAdminCommand.id() + " not found"));


    userServiceCore.grantSuperAdmin(user);
  }
}
