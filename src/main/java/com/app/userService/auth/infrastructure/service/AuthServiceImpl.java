package com.app.userService.auth.infrastructure.service;

import com.app.userService.auth.domain.service.AuthService;
import com.app.userService.auth.domain.valueObjects.AuthToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

  private static final long EXPIRATION_TIME = 86400000L;


  @Value("${jwt.secret}")
  private String secretKey;
  @Override
  public AuthToken generateToken(String userId, List<String> roles) {
    long expirationMillis = System.currentTimeMillis() + EXPIRATION_TIME;
    Date expirationDate = new Date(expirationMillis);

    String token = Jwts.builder()
      .setSubject(userId)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
      .claim("roles", roles)
      .signWith(getSecretKey())
      .compact();

    return new AuthToken(token, expirationDate);
  }

  @Override
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder()
        .setSigningKey(getSecretKey())
        .build()
        .parseClaimsJws(token);
      return true;
    } catch (Exception e) {

      return false;
    }
  }

  @Override
  public String extractSubject(String token) {
    Claims claims = Jwts.parserBuilder()
      .setSigningKey(getSecretKey())
      .build()
      .parseClaimsJws(token)
      .getBody();
    return claims.getSubject();
  }

  private Key getSecretKey() {
    return io.jsonwebtoken.security.Keys.hmacShaKeyFor(this.secretKey.getBytes());
  }
}

