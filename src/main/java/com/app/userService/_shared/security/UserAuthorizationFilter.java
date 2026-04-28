package com.app.userService._shared.security;

import com.app.userService.user.domain.model.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;


@Component
public class UserAuthorizationFilter {
  private static final Logger logger = LoggerFactory.getLogger(UserAuthorizationFilter.class);

  public AuthorizationDecision hasAccessToUser(Authentication authentication, String userId) {
    logger.info("hasAccessToUser {} {}", authentication.getAuthorities(), authentication.getName());
    boolean granted = authentication.getName().equals(userId)
        || hasRole(authentication, Role.ROLE_ADMIN)
        || hasRole(authentication, Role.ROLE_SUPER_ADMIN);
    return new AuthorizationDecision(granted);
  }

  public AuthorizationDecision hasAccessAdmin(Authentication authentication) {
    boolean granted = hasRole(authentication, Role.ROLE_ADMIN) || hasRole(authentication, Role.ROLE_SUPER_ADMIN);
    return new AuthorizationDecision(granted);
  }

  public AuthorizationDecision hasAccessSuperAdmin(Authentication authentication) {
    return new AuthorizationDecision(hasRole(authentication, Role.ROLE_SUPER_ADMIN));
  }

  private boolean hasRole(Authentication authentication, Role role) {
    return authentication.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals(role.name()));
  }
}
