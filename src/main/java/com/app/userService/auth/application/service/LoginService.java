package com.app.userService.auth.application.service;

import com.app.userService._shared.infraestructure.exceptions.InvalidPasswordException;
import com.app.userService._shared.infraestructure.exceptions.UserLockedException;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService._shared.application.service.UserEventService;
import com.app.userService.user.application.service.UserPasswordService;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserAction;
import com.app.userService.user.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
  private final UserPasswordService userPasswordService;
  private final UserEventService userEventService;
  private final UserActionLogService userActionLogService;
  private final UserRepository userRepository;
  public LoginService(UserRepository userRepository,
                      UserPasswordService userPasswordService,
                      UserEventService userEventService,
                      UserActionLogService userActionLogService) {
    this.userRepository = userRepository;
    this.userPasswordService = userPasswordService;
    this.userEventService = userEventService;
    this.userActionLogService = userActionLogService;
  }
  public void login(User user, String passwordInput){
    if (user.isLocked()) {
      throw new UserLockedException("Your account has been locked.");
    }

    boolean isCredentialsValid = this.userPasswordService.isPasswordValid(passwordInput, user.getPassword());

    if (!isCredentialsValid) {
      this.updateFailedLoginAttempts(user);
      userRepository.save(user);

      if (user.isLocked()) {
        logoutUser(user);
        this.userActionLogService.registerUserAction(user, UserAction.LOCKED);
        this.userEventService.handleUserLockedEvent(user);
        throw new UserLockedException("Your account has been locked due to too many failed login attempts.");
      }

      this.userActionLogService.addMetadata("LoginAttemps", user.getFailedLoginAttempts().toString());
      this.userActionLogService.registerUserAction(user, UserAction.ERROR_LOGIN);
      throw new InvalidPasswordException("The current password provided is incorrect.");
    }

    user.clearFailedLoginAttempts();
    userRepository.save(user);
  }
  public void logoutUser(User user){
    user.logout();
    userRepository.save(user);
  }
  private void updateFailedLoginAttempts(User user) {
    user.registerFailedLoginAttempt();
    userRepository.save(user);
  }
}
