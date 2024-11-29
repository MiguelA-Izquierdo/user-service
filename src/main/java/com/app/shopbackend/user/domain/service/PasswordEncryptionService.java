package com.app.shopbackend.user.domain.service;

public interface PasswordEncryptionService {
  String encrypt(String password);
  boolean matches(String rawPassword, String encodedPassword);
}
