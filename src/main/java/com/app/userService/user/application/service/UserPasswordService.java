package com.app.userService.user.application.service;


import com.app.userService._shared.infraestructure.exceptions.InvalidPasswordException;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.repositories.UserRepository;
import com.app.userService.user.domain.service.PasswordEncryptionService;
import org.springframework.stereotype.Service;


@Service
public class UserPasswordService {
  private final PasswordEncryptionService passwordEncryptionService;
  private final UserRepository userRepository;
  public UserPasswordService(PasswordEncryptionService passwordEncryptionService, UserRepository userRepository) {
    this.passwordEncryptionService = passwordEncryptionService;
    this.userRepository = userRepository;
  }
  public void updatePassword(User user, String currentPassword, String newPassword){
    boolean isValidCurrentPassword = this.isPasswordValid(currentPassword, user.getPassword());
    if(!isValidCurrentPassword){
      throw new InvalidPasswordException("The current password provided is incorrect.");
    }
    String hashedPassword = this.encryptPassword(newPassword);
    user.updatePassword(hashedPassword);
    this.userRepository.save(user);
  }
  public void resetPassword(User user, String newPassword){
    String hashedPassword = this.encryptPassword(newPassword);
    user.updatePassword(hashedPassword);
    this.userRepository.save(user);
  }
  public String encryptPassword(String password){
    return this.passwordEncryptionService.encrypt(password);
  }
  public boolean isPasswordValid(String rawPassword, String storedPassword) {
    return this.passwordEncryptionService.matches(rawPassword, storedPassword);
  }
}
