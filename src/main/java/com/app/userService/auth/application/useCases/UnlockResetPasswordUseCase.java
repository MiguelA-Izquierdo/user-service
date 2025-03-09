package com.app.userService.auth.application.useCases;


import com.app.userService.auth.application.bus.command.UnlockResetPasswordCommand;
import com.app.userService.auth.application.service.PasswordRestTokenService;
import com.app.userService.user.application.service.UserPasswordService;
import com.app.userService.auth.domain.exceptions.TokenExpiredException;
import com.app.userService.auth.domain.model.PasswordResetToken;
import com.app.userService.user.application.service.UserActionLogService;
import com.app.userService.user.application.service.UserServiceCore;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserAction;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;


@Service
public class UnlockResetPasswordUseCase {
  private final UserServiceCore userServiceCore;
  private final UserPasswordService userPasswordService;
  private final PasswordRestTokenService passwordRestTokenService;
  private final UserActionLogService userActionLogService;
  public UnlockResetPasswordUseCase(UserServiceCore userServiceCore,
                                    PasswordRestTokenService passwordRestTokenService,
                                    UserPasswordService userPasswordService,
                                    UserActionLogService userActionLogService){
    this.userServiceCore = userServiceCore;
    this.userPasswordService = userPasswordService;
    this.passwordRestTokenService = passwordRestTokenService;
    this.userActionLogService = userActionLogService;
  }
  @Transactional
  public void execute(UnlockResetPasswordCommand command) {
    PasswordResetToken passwordResetToken = passwordRestTokenService.findByToken(command.token())
      .orElseThrow(() -> new EntityNotFoundException("Token not found."));

    if (!passwordResetToken.isValid()) {
      throw new TokenExpiredException("Token has expired.");
    }

    passwordRestTokenService.markAsUsed(passwordResetToken);

    User user = passwordResetToken.getUser();
    userPasswordService.resetPassword(user, command.newPassword());
    userServiceCore.unlockAccount(user);

    userActionLogService.registerUserAction(user, UserAction.UNLOCKED);
  }
}
