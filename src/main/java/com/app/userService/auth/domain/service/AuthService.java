package com.app.userService.auth.domain.service;

import com.app.userService.auth.domain.valueObjects.AuthToken;
import com.app.userService.user.domain.model.User;

import java.util.List;

public interface AuthService {
  AuthToken generateToken(String userId, List<String> roles);
  AuthToken generateToken(User user);
  boolean validateToken(String token);
  String extractSubject(String token);
}
