package com.app.shopbackend.user.application.useCases;

import com.app.shopbackend.user.application.bus.command.UpdatePasswordCommand;
import com.app.shopbackend.user.application.service.UserService;
import com.app.shopbackend.user.domain.model.User;
import com.app.shopbackend.user.domain.valueObjects.UserId;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
public class UpdatePasswordUseCase {

  private final UserService userService;
  public UpdatePasswordUseCase(UserService userService){
    this.userService = userService;
  }
  @Transactional
  public void execute(UpdatePasswordCommand updatePasswordCommand) {
    Optional<User> userOptional = userService.findUserById(UserId.of(updatePasswordCommand.id()));

    User user = userOptional.orElseThrow(() ->
      new EntityNotFoundException("User with ID " + updatePasswordCommand.id() + " not found")
    );

    userService.updatePassword(user, updatePasswordCommand.currentPassword(), updatePasswordCommand.newPassword());
  }
}
