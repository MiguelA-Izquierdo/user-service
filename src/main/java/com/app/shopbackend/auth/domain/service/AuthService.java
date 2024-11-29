package com.app.shopbackend.auth.domain.service;

import com.app.shopbackend.auth.domain.valueObjects.AuthToken;

import java.util.List;

public interface AuthService {
  AuthToken generateToken(String userId, List<String> roles);
  boolean validateToken(String token);
  String extractSubject(String token);
}
