package com.app.shopbackend._shared.security;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class SecurityPropertiesService {

  private static final String[] PUBLIC_POST_ROUTES = {
    "/",
    "/users/password",
    "/auth"
  };

  private static final String[] PUBLIC_GET_ROUTES = {
  };

  private static final String[] PUBLIC_PUT_ROUTES = {
  };

  private static final String[] PUBLIC_PATCH_ROUTES = {
    "/users/password"
  };

  public String[] getPublicPostRoutes() {
    return PUBLIC_POST_ROUTES;
  }

  public String[] getPublicGetRoutes() {
    return PUBLIC_GET_ROUTES;
  }

  public String[] getPublicPutRoutes() {
    return PUBLIC_PUT_ROUTES;
  }

  public String[] getPublicPatchRoutes() {
    return PUBLIC_PATCH_ROUTES;
  }
  public boolean isPublicRoute(HttpServletRequest request) {
    String requestUri = request.getRequestURI();
    String method = request.getMethod();

    if ("POST".equalsIgnoreCase(method)) {
      for (String route : PUBLIC_POST_ROUTES) {
        AntPathRequestMatcher matcher = new AntPathRequestMatcher(route);
        if (matcher.matches(request)) {
          return true;
        }
      }
    }

    if ("GET".equalsIgnoreCase(method)) {
      for (String route : PUBLIC_GET_ROUTES) {
        AntPathRequestMatcher matcher = new AntPathRequestMatcher(route);
        if (matcher.matches(request)) {
          return true;
        }
      }
    }

    return false;
  }
}
