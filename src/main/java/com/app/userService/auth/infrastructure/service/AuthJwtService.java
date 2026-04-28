package com.app.userService.auth.infrastructure.service;

import com.app.userService.auth.domain.service.AuthService;
import com.app.userService.auth.domain.valueObjects.AuthToken;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.UserWrapper;
import com.app.userService.user.domain.valueObjects.UserId;

import com.app.userService.user.application.service.UserServiceCore;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthJwtService implements AuthService {

  private static final Logger logger = LoggerFactory.getLogger(AuthJwtService.class);
  private static final long EXPIRATION_TIME = 86400000L;
  private final UserServiceCore userService;
  private final ObjectMapper objectMapper;

  @Value("${jwt.secret}")
  private String generalSecret;

  public AuthJwtService(UserServiceCore userService, ObjectMapper objectMapper) {
    this.userService = userService;
    this.objectMapper = objectMapper;
  }

  @Override
  public AuthToken generateToken(User user) {
    String userId = user.getId().toString();
    String secretKey = generateMixedSecret(userId);

    Date expirationDate = new Date(System.currentTimeMillis() + EXPIRATION_TIME);

    String token = Jwts.builder()
      .setSubject(userId)
      .setIssuedAt(new Date())
      .setExpiration(expirationDate)
      .claim("roles", user.getRoles().stream().map(Enum::name).toList())
      .signWith(getSecretKey(secretKey))
      .compact();

    return new AuthToken(token, expirationDate);
  }
  @Override
  public Optional<Claims> validateAndExtractClaims(String token) {
    try {
      String userId = extractSubjectUnchecked(token);
      String secretKey = generateMixedSecret(userId);
      if (secretKey == null) return Optional.empty();
      Claims claims = Jwts.parserBuilder()
        .setSigningKey(getSecretKey(secretKey))
        .build()
        .parseClaimsJws(token)
        .getBody();
      return Optional.of(claims);
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  @Override
  public String extractSubjectUnchecked(String token) {
    try {
      String[] parts = token.split("\\.");
      if (parts.length < 2) {
        throw new IllegalArgumentException("Invalid JWT token format");
      }
      String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);
      Map<String, Object> claims = objectMapper.readValue(payloadJson, new TypeReference<>() {});
      return (String) claims.get("sub");
    } catch (Exception e) {
      throw new RuntimeException("Error extracting subject from token", e);
    }
  }

  private String generateMixedSecret(String userId) {
    try {
      UUID.fromString(userId);
    } catch (IllegalArgumentException e) {
      return null;
    }
    UserWrapper userWrapper = this.userService.findUserById(UserId.of(userId));
    return userWrapper.getUser()
      .map(u -> hashSecret(generalSecret + u.getSecretKey()))
      .orElse(null);
  }

  private SecretKey getSecretKey(String secret) {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  private String hashSecret(String input) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hash);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Error generando hash de clave secreta", e);
    }
  }

}
