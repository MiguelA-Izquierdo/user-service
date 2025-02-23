package com.app.userService.auth.application.service;

import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginAttemptService {
  private final UserRepository userRepository;
  public LoginAttemptService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void updateFailedLoginAttempts(User user) {
    user.registerFailedLoginAttempt();
    userRepository.save(user);
  }
}
