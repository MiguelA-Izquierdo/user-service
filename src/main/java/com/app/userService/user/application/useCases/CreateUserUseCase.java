package com.app.userService.user.application.useCases;

import com.app.userService.user.application.bus.command.CreateUserCommand;
import com.app.userService.user.application.bus.event.UserCreatedEvent;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.event.UserEvent;
import com.app.userService.user.domain.model.Role;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserStatus;
import com.app.userService.user.domain.service.EventPublisher;
import com.app.userService.user.domain.valueObjects.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CreateUserUseCase {

  private final UserServiceCore userServiceCore;
  private final EventPublisher userPublisher;
  public CreateUserUseCase(UserServiceCore userServiceCore, EventPublisher userPublisher){
    this.userServiceCore = userServiceCore;
    this.userPublisher = userPublisher;
  }
  @Transactional
  public void execute(CreateUserCommand command) {
    User user = createUserFromCommand(command);
    userServiceCore.createUser(user);
    UserEvent userEvent = UserCreatedEvent.of(user);
    userPublisher.publish(userEvent);
  }

  private User createUserFromCommand(CreateUserCommand command){
    UserId userId = UserId.of(command.id());
    UserName userName = UserName.of(command.name());
    UserLastName userLastName = UserLastName.of(command.lastName());
    UserEmail userEmail = UserEmail.of(command.email());
    Phone phone = Phone.of(command.countryCode(), command.number());
    IdentityDocument identityDocument = IdentityDocument.of(command.documentType(), command.documentNumber());
    Address address = Address.of(
      command.street(),
      command.streetNumber(),
      command.city(),
      command.state(),
      command.postalCode(),
      command.country());

    String passwordHashed = userServiceCore.encryptPassword(command.password());

    List<Role> emptyRolesList = new ArrayList<>();
    return new User(userId, userName, userLastName, userEmail, identityDocument, phone, address, passwordHashed, LocalDateTime.now(), UserStatus.ACTIVE,emptyRolesList);
  }
}
