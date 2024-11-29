package com.app.shopbackend.user.application.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserAuthorizationService {

  public boolean canAccessUser(Authentication authentication, String requestedUserId) {
    String authenticatedUserId = (String) authentication.getPrincipal(); // Obtenemos el usuario autenticado desde el token

    return authenticatedUserId.equals(requestedUserId) || hasRoleAdmin(authentication);
  }

  private boolean hasRoleAdmin(Authentication authentication) {
    return authentication.getAuthorities().stream()
      .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
  }
}
