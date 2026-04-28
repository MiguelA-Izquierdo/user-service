package com.app.userService.user.application.service;

import com.app.userService.user.application.bus.command.UpdateUserCommand;
import com.app.userService.user.domain.exceptions.RoleAlreadyGrantedException;
import com.app.userService.user.domain.exceptions.UserAlreadyExistsException;
import com.app.userService.user.domain.model.*;
import com.app.userService.user.domain.repositories.UserRepository;
import com.app.userService.user.domain.repositories.UserRoleRepository;
import com.app.userService.user.domain.valueObjects.*;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class UserServiceCore {
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

  @Transactional
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
    Map<String, String> changes = new LinkedHashMap<>();
    User updatedUser = updateUserFields(command, currentUser, changes);

    if (!updatedUser.equals(currentUser)) {
      changes.forEach(userActionLogService::addMetadata);
      userRepository.save(updatedUser);
    }
  }
  private User updateUserFields(UpdateUserCommand command, User current, Map<String, String> changes) {
    return User.of(
      current.getId(),
      resolveField(command.userName(), UserName::of, current.getName(), "Name", changes),
      resolveField(command.lastName(), UserLastName::of, current.getLastName(), "LastName", changes),
      current.getEmail(),
      resolveField(command.identityDocument(),
        d -> IdentityDocument.of(d.documentType(), d.documentNumber()),
        current.getIdentityDocument(), "Document", changes),
      resolveField(command.phone(),
        p -> Phone.of(p.countryCode(), p.phoneNumber()),
        current.getPhone(), "Phone number", changes),
      resolveField(command.address(),
        a -> Address.of(a.street(), a.streetNumber(), a.city(), a.state(), a.postalCode(), a.country()),
        current.getAddress(), "Address", changes),
      current.getPassword(),
      current.getFailedLoginAttempts(),
      current.getSecretKey(),
      current.getCreatedAt(),
      current.getStatus(),
      current.getRoles()
    );
  }
  private <T, R> R resolveField(T commandValue, Function<T, R> factory, R current,
                                 String fieldName, Map<String, String> changes) {
    if (commandValue == null) return current;
    R newValue = factory.apply(commandValue);
    if (!newValue.equals(current)) {
      changes.put(fieldName + " old value:", String.valueOf(current));
      changes.put(fieldName + " new value:", String.valueOf(newValue));
    }
    return newValue;
  }
  public void unlockAccount(User user){
    user.unlockAccount();
    userRepository.save(user);
  }
}
