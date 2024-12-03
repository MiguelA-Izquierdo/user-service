package com.app.userService.user.application.useCases;

import com.app.userService.user.application.bus.command.DeleteUserCommand;
import com.app.userService.user.application.bus.command.handle.DeleteUserCommandHandler;
import com.app.userService.user.application.bus.event.UserDeletedEvent;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.event.UserEvent;
import com.app.userService.user.domain.exceptions.UserAlreadyExistsException;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.service.EventPublisher;
import com.app.userService.user.domain.valueObjects.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DeleteUserUseCase {
  private static final Logger logger = LoggerFactory.getLogger(DeleteUserUseCase.class);

  private final UserServiceCore userServiceCore;
  private final EventPublisher userPublisher;
  public DeleteUserUseCase(UserServiceCore userServiceCore, EventPublisher userPublisher){
    this.userServiceCore = userServiceCore;
    this.userPublisher = userPublisher;
  }
  @Transactional
  public void execute(DeleteUserCommand command) {
    UserWrapper existingUser = userServiceCore.findUserById(UserId.of(command.id()));

    if (!existingUser.exists() || !existingUser.isActive()) {
      throw new EntityNotFoundException("User with ID " + command.id() + " not found");
    }

    User user = existingUser.getUser()
      .orElseThrow(() -> new EntityNotFoundException("User with ID " + command.id() + " not found"));


    userServiceCore.anonymizeUser(user);
    UserEvent userEvent = UserDeletedEvent.of(user);
    userPublisher.publish(userEvent);
  }


}
