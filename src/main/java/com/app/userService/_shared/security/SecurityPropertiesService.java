package com.app.userService._shared.security;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Component
public class SecurityPropertiesService {

  private static final String[] PUBLIC_POST_ROUTES = {
    "/users",
    "/auth"
  };

  private static final String[] PUBLIC_GET_ROUTES = {
    "/swagger-ui/**",
    "/v3/api-docs/**",
    "/swagger-resources/**",
    "/favicon.ico"
  };

  private static final String[] PUBLIC_PUT_ROUTES = {
  };

  private static final String[] PUBLIC_PATCH_ROUTES = {
    "/users/password"
  };

  private static final String[] PUBLIC_DELETE_ROUTES = {
  };

  private final List<AntPathRequestMatcher> publicMatchers = new ArrayList<>();

  public SecurityPropertiesService() {
    initializeMatchers();
  }

  private void initializeMatchers() {
    addMatchers(PUBLIC_POST_ROUTES, "POST");
    addMatchers(PUBLIC_GET_ROUTES, "GET");
    addMatchers(PUBLIC_PUT_ROUTES, "PUT");
    addMatchers(PUBLIC_PATCH_ROUTES, "PATCH");
    addMatchers(PUBLIC_DELETE_ROUTES, "DELETE");
  }

  private void addMatchers(String[] routes, String method) {
    for (String route : routes) {
      publicMatchers.add(new AntPathRequestMatcher(route, method));
    }
  }

  public boolean isPublicRoute(HttpServletRequest request) {
    return publicMatchers.stream().anyMatch(matcher -> matcher.matches(request));
  }
}
