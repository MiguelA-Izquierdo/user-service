package com.app.userService.auth.domain.service;

import com.app.userService.auth.domain.valueObjects.AuthToken;
import com.app.userService.user.domain.model.User;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Optional;

public interface AuthService {
  AuthToken generateToken(User user);
  Optional<Claims> validateAndExtractClaims(String token);
  String extractSubjectUnchecked(String token);
  boolean isAdmin(Collection<? extends GrantedAuthority> authorities);
}
