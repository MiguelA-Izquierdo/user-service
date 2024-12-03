package com.app.userService._shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import io.jsonwebtoken.Jwts;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
  private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);


  @Value("${jwt.secret}")
  private String secretKey;

  private final SecurityPropertiesService securityPropertiesService;

  public JwtAuthenticationFilter(SecurityPropertiesService securityPropertiesService) {
    this.securityPropertiesService = securityPropertiesService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
    throws ServletException, IOException {

    logger.info("Processing request: {} {}", request.getRequestURI(), request.getMethod());

    String requestUri = request.getRequestURI();
    String method = request.getMethod();

    if (securityPropertiesService.isPublicRoute(request)) {
      filterChain.doFilter(request, response);
      return;
    }

    String token = extractTokenFromHeader(request);
    if (token == null) {
      sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token not provided");
      return;
    }

    try {
      Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSecretKey())
        .build()
        .parseClaimsJws(token)
        .getBody();

      String userId = claims.getSubject();
      List<GrantedAuthority> authorities = extractAuthoritiesFromClaims(claims);

      UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
        userId, null, authorities);
      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (Exception e) {
      logger.error("Token validation failed", e);
      sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "Token not valid");
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
        .map(role -> new SimpleGrantedAuthority((String) role)) // Se asume que los roles ya tienen el prefijo "ROLE_"
        .collect(Collectors.toList());
    }
    return Collections.emptyList();
  }

  private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
    Map<String, Object> errorResponse = new HashMap<>();
    errorResponse.put("status", "error");
    errorResponse.put("code", status.value());
    errorResponse.put("message", message);

    response.setStatus(status.value());
    response.setContentType("application/json");

    ObjectMapper objectMapper = new ObjectMapper();
    response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
  }

  private Key getSecretKey() {
    return io.jsonwebtoken.security.Keys.hmacShaKeyFor(secretKey.getBytes());
  }
}