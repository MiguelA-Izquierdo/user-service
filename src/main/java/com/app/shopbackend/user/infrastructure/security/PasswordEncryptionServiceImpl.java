package com.app.shopbackend.user.infrastructure.security;


import com.app.shopbackend.user.domain.service.PasswordEncryptionService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordEncryptionServiceImpl implements PasswordEncryptionService {

  private final PasswordEncoder passwordEncoder;

  public PasswordEncryptionServiceImpl() {
    this.passwordEncoder = new BCryptPasswordEncoder();
  }

  @Override
  public String encrypt(String password) {
    return passwordEncoder.encode(password);
  }

  @Override
  public boolean matches(String rawPassword, String encodedPassword) {
    return passwordEncoder.matches(rawPassword, encodedPassword);
  }
}
