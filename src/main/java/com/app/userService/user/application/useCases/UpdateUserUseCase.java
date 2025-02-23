package com.app.userService.user.application.useCases;

import com.app.userService.user.application.bus.command.UpdateUserCommand;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.Role;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserStatus;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.valueObjects.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UpdateUserUseCase {

  private final UserServiceCore userServiceCore;
  public UpdateUserUseCase(UserServiceCore userServiceCore){
    this.userServiceCore = userServiceCore;
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

  }

  private void getUpdatedUser(UpdateUserCommand command, User currentUser) {
    Map<String, Object> changedFields = new HashMap<>();
    UserId userId = currentUser.getId();
    UserName userName = currentUser.getName();
    UserLastName lastName = currentUser.getLastName();
    UserEmail userEmail = currentUser.getEmail();
    IdentityDocument identityDocument = currentUser.getIdentityDocument();
    Phone phone = currentUser.getPhone();
    Address address = currentUser.getAddress();
    String password = currentUser.getPassword();
    String secretkey = currentUser.getSecretKey();
    LocalDateTime dateCreated = currentUser.getCreatedAt();
    UserStatus status = currentUser.getStatus();
    List<Role> roles = currentUser.getRoles();

    boolean hasChanges = false;

    UserName updatedUserName = command.userName() != null ? UserName.of(command.userName()) : userName;
    if (!userName.equals(updatedUserName)) {
      hasChanges = true;
      userName = UserName.of(command.userName());
    }

    UserLastName updatedUserLastName = command.lastName() != null
      ? UserLastName.of(command.lastName())
      : lastName;
    if (!lastName.equals(updatedUserLastName)) {
      hasChanges = true;
      lastName = UserLastName.of(command.lastName());
    }

    IdentityDocument updatedIdentityDocument = command.identityDocument() != null
      ? IdentityDocument.of(command.identityDocument().documentType(), command.identityDocument().documentNumber())
      : identityDocument;
    if (!identityDocument.equals(updatedIdentityDocument)) {
      hasChanges = true;
      identityDocument = IdentityDocument.of(command.identityDocument().documentType(), command.identityDocument().documentNumber());
    }

    Phone updatedPhone = command.phone() != null
      ? Phone.of(command.phone().countryCode(), command.phone().phoneNumber())
      : phone;
    if (!phone.equals(updatedPhone)) {
      hasChanges = true;
      phone = Phone.of(command.phone().countryCode(), command.phone().phoneNumber());
    }

    Address updatedAddress = command.address() != null
      ? Address.of(command.address().street(), command.address().streetNumber(),command.address().city(),command.address().state(), command.address().postalCode(), command.address().country())
      : address;
    if (!address.equals(updatedAddress)) {
      hasChanges = true;
      address = Address.of(command.address().street(), command.address().streetNumber(),command.address().city(),command.address().state(), command.address().postalCode(), command.address().country());
    }

    if (hasChanges) {
      User.of(userId, userName, lastName, userEmail, identityDocument, phone, address, password, currentUser.getFailedLoginAttempts(),secretkey, dateCreated, status, roles);
    }

  }

  private boolean hasChanged(Object newValue, Object currentValue) {
    return newValue != null && !newValue.toString().isEmpty() && !newValue.equals(currentValue);
  }
}
