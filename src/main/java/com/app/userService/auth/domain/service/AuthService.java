package com.app.userService.auth.domain.service;

import com.app.userService.auth.domain.valueObjects.AuthToken;
import com.app.userService.user.domain.model.User;
import io.jsonwebtoken.Claims;

import java.util.Optional;

public interface AuthService {
  AuthToken generateToken(User user);
  Optional<Claims> validateAndExtractClaims(String token);
  String extractSubjectUnchecked(String token);
}
