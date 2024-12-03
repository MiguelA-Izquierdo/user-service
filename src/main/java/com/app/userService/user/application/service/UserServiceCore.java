package com.app.userService.user.application.service;

import com.app.userService._shared.exceptions.InvalidPasswordException;
import com.app.userService.user.application.dto.UserAuthDTO;
import com.app.userService.user.domain.exceptions.RoleAlreadyGrantedException;
import com.app.userService.user.domain.exceptions.UserAlreadyExistsException;
import com.app.userService.user.domain.model.*;
import com.app.userService.user.domain.repositories.UserRepository;
import com.app.userService.user.domain.repositories.UserRoleRepository;
import com.app.userService.user.domain.service.PasswordEncryptionService;
import com.app.userService.user.domain.valueObjects.UserId;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class UserServiceCore {
  private static final Logger logger = LoggerFactory.getLogger(UserServiceCore.class);

  private final PasswordEncryptionService passwordEncryptionService;
  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  public UserServiceCore(PasswordEncryptionService passwordEncryptionService,
                         UserRepository userRepository,
                         UserRoleRepository userRoleRepository) {
    this.userRepository = userRepository;
    this.userRoleRepository = userRoleRepository;
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
  public UserWrapper findUserById(UserId id){
    return this.userRepository.findById(id.getValue());
  }
  public Optional<UserAuthDTO> findUserByEmail(String email){
    return this.userRepository.findByEmail(email).map(UserAuthDTO::Of);
  }
  public PaginatedResult<User> findAll(Integer  page, Integer size){
    return this.userRepository.findAll(page, size);
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
  public void anonymizeUser(User user){
    AnonymousUser anonymousUser = new AnonymousUser(
      user.getId(),
      user.getName().getValue(),
      user.getLastName().getValue(),
      user.getEmail().getEmail(),
      user.getIdentityDocument().getDocumentType(),
      user.getIdentityDocument().getDocumentNumber(),
      user.getPhone().getCountryCode(),
      user.getPhone().getNumber(),
      user.getAddress().getStreet(),
      user.getAddress().getNumber(),
      user.getAddress().getCity(),
      user.getAddress().getState(),
      user.getAddress().getPostalCode(),
      user.getAddress().getCountry(),
      user.getPassword(),
      user.getCreatedAt(),
      UserStatus.DELETED,
      new ArrayList<>()
    );

    userRepository.anonymize(anonymousUser);
    userRoleRepository.deleteByUser(user);
  }
  public boolean verifyPassword(String rawPassword, String storedPassword) {
    return this.passwordEncryptionService.matches(rawPassword, storedPassword);
  }
  public String encryptPassword(String password){
    return this.passwordEncryptionService.encrypt(password);
  }
  public boolean isSuperAdmin(String userId) {
    UserId id = UserId.of(userId);
    UserWrapper existingUser = this.findUserById(UserId.of(userId));

    if (!existingUser.exists() || !existingUser.isActive()) {
      throw new EntityNotFoundException("User with ID " + userId + " not found");
    }

    User user = existingUser.getUser()
      .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));

    return user.getRoles().stream()
      .anyMatch(role -> role.name().equals("ROLE_SUPER_ADMIN"));
  }
}
