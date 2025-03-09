package com.app.userService.user.application.useCases;

import com.app.userService.user.application.service.UserPasswordService;
import com.app.userService.user.application.bus.command.CreateUserCommand;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService._shared.application.service.UserEventService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.Role;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserAction;
import com.app.userService.user.domain.model.UserStatus;
import com.app.userService.user.domain.valueObjects.*;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreateUserUseCase {
  private final UserServiceCore userServiceCore;
  private final UserPasswordService userPasswordService;
  private final UserEventService userEventService;
  private final UserActionLogService userActionLogService;
  public CreateUserUseCase(UserServiceCore userServiceCore, UserPasswordService userPasswordService, UserEventService userEventService, UserActionLogService userActionLogService){
    this.userServiceCore = userServiceCore;
    this.userEventService = userEventService;
    this.userActionLogService = userActionLogService;
    this.userPasswordService = userPasswordService;
  }
  @Transactional
  public void execute(CreateUserCommand command) {
    User user = createUserFromCommand(command);
    userServiceCore.registerUser(user);

    userActionLogService.registerUserAction(user, UserAction.CREATED);

    userEventService.handleUserCreatedEvent(user);
  }

  private User createUserFromCommand(CreateUserCommand command){
    Integer failedLoginAttempts = 0;
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

    String passwordHashed = userPasswordService.encryptPassword(command.password());

    List<Role> emptyRolesList = new ArrayList<>();
    return User.of(userId, userName, userLastName, userEmail, identityDocument, phone, address, passwordHashed, failedLoginAttempts, LocalDateTime.now(), UserStatus.ACTIVE,emptyRolesList);
  }

}

