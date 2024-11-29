package com.app.shopbackend.user.application.service;

import com.app.shopbackend._shared.exceptions.InvalidPasswordException;
import com.app.shopbackend.user.application.dto.UserAuthDTO;
import com.app.shopbackend.user.domain.exceptions.RoleAlreadyGrantedException;
import com.app.shopbackend.user.domain.exceptions.UserAlreadyExistsException;
import com.app.shopbackend.user.domain.model.Role;
import com.app.shopbackend.user.domain.model.User;
import com.app.shopbackend.user.domain.repositories.UserRepository;
import com.app.shopbackend.user.domain.repositories.UserRoleRepository;
import com.app.shopbackend.user.domain.service.PasswordEncryptionService;
import com.app.shopbackend.user.domain.valueObjects.UserId;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

  private final PasswordEncryptionService passwordEncryptionService;
  private final UserRepository userRepository;
  private final UserRoleRepository userRoleRepository;
  public UserService(PasswordEncryptionService passwordEncryptionService,
                     UserRepository userRepository,
                     UserRoleRepository userRoleRepository) {
    this.userRepository = userRepository;
    this.userRoleRepository = userRoleRepository;
    this.passwordEncryptionService = passwordEncryptionService;
  }

  public void createUser(User user) {
    Optional<User> existingUser = this.userRepository.findByIdOrEmail(
      user.getId().getValue(),
      user.getEmail().getEmail()
    );

    if (existingUser.isPresent()) {
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
  public Optional<User> findUserById(UserId id){
    return this.userRepository.findById(id.getValue());
  }
  public Optional<UserAuthDTO> findUserByEmail(String email){
    return this.userRepository.findByEmail(email).map(UserAuthDTO::Of);
  }

  public List<User> findAll(Integer  page, Integer size){
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
  public boolean verifyPassword(String rawPassword, String storedPassword) {
    return this.passwordEncryptionService.matches(rawPassword, storedPassword);
  }
  public String encryptPassword(String password){
    return this.passwordEncryptionService.encrypt(password);
  }


  public boolean isSuperAdmin(String userId) {
    UserId id = UserId.of(userId);
    User user = userRepository.findById(id.getValue())
      .orElseThrow(() -> new EntityNotFoundException("User not found"));

    return user.getRoles().stream()
      .anyMatch(role -> role.name().equals("ROLE_SUPER_ADMIN"));
  }
}
