package com.app.userService.auth.domain.service;

import com.app.userService.auth.domain.valueObjects.AuthToken;

import java.util.List;

public interface AuthService {
  AuthToken generateToken(String userId, List<String> roles);
  boolean validateToken(String token);
  String extractSubject(String token);
}
