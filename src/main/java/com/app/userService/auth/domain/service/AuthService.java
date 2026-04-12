package com.app.userService.auth.domain.service;

import com.app.userService.auth.domain.valueObjects.AuthToken;
import com.app.userService.user.domain.model.User;
import io.jsonwebtoken.Claims;

public interface AuthService {
  AuthToken generateToken(User user);
  boolean validateToken(String token);
  String extractSubjectUnchecked(String token);
  public Claims getClaimsFromToken(String token);
}
