package com.app.userService._shared.security;

import com.app.userService._shared.application.service.RequestContext;
import com.app.userService._shared.infraestructure.dto.ErrorResponseDTO;
import com.app.userService.auth.domain.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.Claims;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
  private final AuthService authService;
  @Value("${jwt.secret}")
  private String secretKey;

  private final SecurityPropertiesService securityPropertiesService;
  private final RequestContext requestContext;

  public JwtAuthenticationFilter(SecurityPropertiesService securityPropertiesService,
                                 AuthService authService,
                                 RequestContext requestContext) {
    this.securityPropertiesService = securityPropertiesService;
    this.authService = authService;
    this.requestContext = requestContext;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  @NotNull HttpServletResponse response,
                                  @NotNull FilterChain filterChain)
    throws ServletException, IOException {

    logger.info("Processing request: {} {}", request.getRequestURI(), request.getMethod());

    if (securityPropertiesService.isPublicRoute(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = extractTokenFromHeader(request);
    if (token == null) {
      sendErrorResponse(response, "Token not provided");
      return;
    }

    try {
      if (!authService.validateToken(token)) {
        sendErrorResponse(response, "Token not valid");
        return;
      }

      Claims claims = authService.getClaimsFromToken(token);
      String userId = claims.getSubject();
      requestContext.setAuthenticatedUserId(userId);
      List<GrantedAuthority> authorities = extractAuthoritiesFromClaims(claims);

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userId, null, authorities);
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (Exception e) {
      logger.error("Token validation failed", e);
      sendErrorResponse(response, "Token not valid");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private String extractTokenFromHeader(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header != null && header.startsWith("Bearer ")) {
      return header.substring(7);
    }
    return null;
  }

  private List<GrantedAuthority> extractAuthoritiesFromClaims(Claims claims) {
    Object rolesObj = claims.get("roles");
    if (rolesObj instanceof List<?> rolesList) {
      return rolesList.stream()
        .filter(role -> role instanceof String)
        .map(role -> new SimpleGrantedAuthority((String) role))
        .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  private void sendErrorResponse(HttpServletResponse response, String message) throws IOException {
    ErrorResponseDTO errorResponse = ErrorResponseDTO.Of(
      HttpStatus.UNAUTHORIZED.value(),
      message
    );

    ObjectMapper objectMapper = new ObjectMapper();
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }
}
