package com.app.userService._shared.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Component
public class UserAuthorizationFilter {
  private static final Logger logger = LoggerFactory.getLogger(UserAuthorizationFilter.class);

  public AuthorizationDecision hasAccessToUser(Authentication authentication, String userId) {
    logger.info("hasAccessToUser {} {}",authentication.getAuthorities(),authentication.getName());

    String authenticatedUserId = authentication.getName();

    boolean hasAdminRole = authentication.getAuthorities().stream()
      .anyMatch(grantedAuthority ->
        grantedAuthority.getAuthority().equals("ROLE_ADMIN") ||
          grantedAuthority.getAuthority().equals("ROLE_SUPER_ADMIN")
      );
    if (authenticatedUserId.equals(userId) || hasAdminRole) {
      return new AuthorizationDecision(true);
    }

    return new AuthorizationDecision(false);
  }

  public AuthorizationDecision hasAccessAdmin(Authentication authentication) {

    boolean hasAdminRole = authentication.getAuthorities().stream()
      .anyMatch(grantedAuthority ->
        grantedAuthority.getAuthority().equals("ROLE_ADMIN") ||
          grantedAuthority.getAuthority().equals("ROLE_SUPER_ADMIN")
      );

    if (hasAdminRole) {
      return new AuthorizationDecision(true);
    }

    return new AuthorizationDecision(false);
  }

  public AuthorizationDecision hasAccessSuperAdmin(Authentication authentication) {

    boolean hasAdminRole = authentication.getAuthorities().stream()
      .anyMatch(grantedAuthority ->
          grantedAuthority.getAuthority().equals("ROLE_SUPER_ADMIN")
      );

    if (hasAdminRole) {
      return new AuthorizationDecision(true);
    }

    return new AuthorizationDecision(false);
  }


  private boolean hasRole(Authentication authentication, String role) {
    return authentication.getAuthorities().stream()
      .anyMatch(authority -> authority.getAuthority().equals(role));
  }
}
