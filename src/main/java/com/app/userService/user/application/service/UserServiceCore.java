package com.app.userService.user.application.service;

import com.app.userService.user.application.bus.command.UpdateUserCommand;
import com.app.userService.user.domain.exceptions.RoleAlreadyGrantedException;
import com.app.userService.user.domain.exceptions.UserAlreadyExistsException;
import com.app.userService.user.domain.model.*;
import com.app.userService.user.domain.repositories.UserRepository;
import com.app.userService.user.domain.repositories.UserRoleRepository;
import com.app.userService.user.domain.valueObjects.*;

import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

@Service
public class UserServiceCore {
  private static final Logger logger = LoggerFactory.getLogger(UserServiceCore.class);
  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final UserActionLogService userActionLogService;
  public UserServiceCore(UserRepository userRepository,
                         UserRoleRepository userRoleRepository,
                         UserActionLogService userActionLogService) {
    this.userRepository = userRepository;
    this.userRoleRepository = userRoleRepository;
    this.userActionLogService = userActionLogService;
  }

  public void registerUser(User user) {
    UserWrapper existingUser = this.userRepository.findByIdOrEmail(
      user.getId().getValue(),
      user.getEmail().getEmail()
    );

    if (existingUser.exists()) {
      throw new UserAlreadyExistsException("The user with the same ID or email already exists.");
    }
    this.userRepository.save(user);
    this.userRoleRepository.save(user, Role.ROLE_USER);
  }
  public void grantSuperAdmin(User user){
    Optional<Role> existingRole = this.userRoleRepository.findRoleByUserIdRoleName(user, Role.ROLE_SUPER_ADMIN);
    if(existingRole.isEmpty()){
      this.userRoleRepository.save(user, Role.ROLE_SUPER_ADMIN);
    } else {
      throw new RoleAlreadyGrantedException("User already has the SUPER_ADMIN role.");
    }
  }
  @Transactional
  public UserWrapper findUserById(UserId id){
    return this.userRepository.findById(id.getValue());
  }
  @Transactional
  public UserWrapper findUserByEmail(String email){
    return this.userRepository.findByEmail(email);
  }
  public PaginatedResult<User> findAll(Integer  page, Integer size){
    return this.userRepository.findAll(page, size);
  }
  public void anonymizeUser(User user){
    AnonymousUser anonymousUser = AnonymousUser.of(user);
    userRepository.anonymize(anonymousUser);
    userRoleRepository.deleteByUser(user);
  }
  public void updateUser(UpdateUserCommand command, User currentUser) {
    User updatedUser = updateUserFields(command, currentUser);

    if (!updatedUser.equals(currentUser)) {
      userRepository.save(updatedUser);
    }
  }
  private User updateUserFields(UpdateUserCommand command, User currentUser) {
    return User.of(
      currentUser.getId(),
      updateIfChanged("Name",command.userName(), currentUser.getName(), UserName::of),
      updateIfChanged("LastName",command.lastName(), currentUser.getLastName(), UserLastName::of),
      currentUser.getEmail(),
      updateIfChanged(
        "Document",
        command.identityDocument(),
        currentUser.getIdentityDocument(),
        doc -> IdentityDocument.of(doc.documentType(), doc.documentNumber())
      ),
      updateIfChanged(
        "Phone number",
        command.phone(),
        currentUser.getPhone(),
        phone -> Phone.of(phone.countryCode(), phone.phoneNumber())
      ),
      updateIfChanged(
        "Address",
        command.address(),
        currentUser.getAddress(),
        addr -> Address.of(addr.street(), addr.streetNumber(), addr.city(), addr.state(), addr.postalCode(), addr.country())
      ),
      currentUser.getPassword(),
      currentUser.getFailedLoginAttempts(),
      currentUser.getSecretKey(),
      currentUser.getCreatedAt(),
      currentUser.getStatus(),
      currentUser.getRoles()
    );
  }
  private <T, R> R updateIfChanged(String field, T newValue, R currentValue, Function<T, R> mapper) {
    if (!Objects.equals(currentValue, newValue)) {
      userActionLogService.addMetadata(field + " old value:", String.valueOf(currentValue));
      userActionLogService.addMetadata(field + " new value:", String.valueOf(newValue));
    }
    return newValue != null ? mapper.apply(newValue) : currentValue;
  }

  public void unlockAccount(User user){
    user.unlockAccount();
    userRepository.save(user);
  }
}
