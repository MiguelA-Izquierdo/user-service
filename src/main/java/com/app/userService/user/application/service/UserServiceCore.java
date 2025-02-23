package com.app.userService.user.application.service;

import com.app.userService._shared.exceptions.InvalidPasswordException;
import com.app.userService._shared.exceptions.UserLockedException;
import com.app.userService.auth.application.service.LoginAttemptService;
import com.app.userService.user.application.bus.command.UpdateUserCommand;
import com.app.userService.user.domain.exceptions.RoleAlreadyGrantedException;
import com.app.userService.user.domain.exceptions.UserAlreadyExistsException;
import com.app.userService.user.domain.model.*;
import com.app.userService.user.domain.repositories.UserRepository;
import com.app.userService.user.domain.repositories.UserRoleRepository;
import com.app.userService.user.domain.service.PasswordEncryptionService;
import com.app.userService.user.domain.valueObjects.*;

import org.springframework.transaction.annotation.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;




import java.util.HashMap;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

@Service
public class UserServiceCore {
  private static final Logger logger = LoggerFactory.getLogger(UserServiceCore.class);

  private final PasswordEncryptionService passwordEncryptionService;
  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  private final UserEventService userEventService;
  private final UserActionLogService userActionLogService;
  private final LoginAttemptService loginAttemptService;
  public UserServiceCore(PasswordEncryptionService passwordEncryptionService,
                         UserRepository userRepository,
                         UserEventService userEventService,
                         UserActionLogService userActionLogService,
                         LoginAttemptService loginAttemptService,
                         UserRoleRepository userRoleRepository) {
    this.userRepository = userRepository;
    this.userRoleRepository = userRoleRepository;
    this.userEventService = userEventService;
    this.userActionLogService = userActionLogService;
    this.loginAttemptService = loginAttemptService;
    this.passwordEncryptionService = passwordEncryptionService;
  }

  public void createUser(User user) {
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
  public UserWrapper findUserByEmail(String email){
    return this.userRepository.findByEmail(email);
  }
  public PaginatedResult<User> findAll(Integer  page, Integer size){
    return this.userRepository.findAll(page, size);
  }
  public void logoutUser(User user){
    String newSecretKey = generateRandomSecretKey();
    user.logout(newSecretKey);
    userRepository.save(user);
  }
  public void updatePassword(User user, String currentPassword, String newPassword){
    boolean isValidCurrentPassword = this.verifyPassword(currentPassword, user.getPassword());
    if(!isValidCurrentPassword){
      throw new InvalidPasswordException("The current password provided is incorrect.");
    }
    String hashedPassword = this.encryptPassword(newPassword);
    user.updatePassword(hashedPassword);
    this.userRepository.save(user);
  }
  public void updatePassword(User user, String newPassword){
    String hashedPassword = this.encryptPassword(newPassword);
    user.updatePassword(hashedPassword);
    this.userRepository.save(user);
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
      updateIfChanged(command.userName(), currentUser.getName(), UserName::of),
      updateIfChanged(command.lastName(), currentUser.getLastName(), UserLastName::of),
      currentUser.getEmail(),
      updateIfChanged(
        command.identityDocument(),
        currentUser.getIdentityDocument(),
        doc -> IdentityDocument.of(doc.documentType(), doc.documentNumber())
      ),
      updateIfChanged(
        command.phone(),
        currentUser.getPhone(),
        phone -> Phone.of(phone.countryCode(), phone.phoneNumber())
      ),
      updateIfChanged(
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
  private <T, R> R updateIfChanged(T newValue, R currentValue, Function<T, R> mapper) {
    return newValue != null ? mapper.apply(newValue) : currentValue;
  }
  public boolean verifyPassword(String rawPassword, String storedPassword) {
    return this.passwordEncryptionService.matches(rawPassword, storedPassword);
  }
  public String encryptPassword(String password){
    return this.passwordEncryptionService.encrypt(password);
  }
  public String generateRandomSecretKey(){
    return new Random()
      .ints(18, 48, 122)
      .filter(Character::isLetterOrDigit)
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString();
  }
  public void login(User user, String passwordInput){
    if (user.isLocked()) {
      throw new UserLockedException("Your account has been locked.");
    }

    boolean isCredentialsValid = this.verifyPassword(passwordInput, user.getPassword());

    if (!isCredentialsValid) {
      this.loginAttemptService.updateFailedLoginAttempts(user);
      userRepository.save(user);

      if (user.isLocked()) {
        logoutUser(user);
        HashMap<String, String> metaData = new HashMap<>();
        this.userActionLogService.registerUserLocked(user, metaData);
        this.userEventService.handleUserLockedEvent(user);
        throw new UserLockedException("Your account has been locked due to too many failed login attempts.");
      }

      throw new InvalidPasswordException("The current password provided is incorrect.");
    }

    user.clearFailedLoginAttempts();
    userRepository.save(user);
  }

  public void unlockUser(User user){
    user.unlockAccount();
    userRepository.save(user);
  }
}
