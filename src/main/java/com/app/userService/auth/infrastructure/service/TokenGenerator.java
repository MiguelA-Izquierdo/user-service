package com.app.userService.auth.infrastructure.service;

import com.app.userService.auth.domain.service.TokenService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
@Service
public class TokenGenerator implements TokenService {
  private static final SecureRandom secureRandom = new SecureRandom();
  private static final int TOKEN_LENGTH = 32;
  @Override
  public String generateToken() {
    byte[] tokenBytes = new byte[TOKEN_LENGTH];
    secureRandom.nextBytes(tokenBytes);
    return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
  }
}
