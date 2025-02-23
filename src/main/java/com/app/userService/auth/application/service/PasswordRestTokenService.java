package com.app.userService.auth.application.service;

import com.app.userService._shared.exceptions.EntityExistsException;
import com.app.userService.auth.domain.model.PasswordResetToken;
import com.app.userService.auth.domain.repositories.PasswordResetTokenRepository;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PasswordRestTokenService {
  private final PasswordResetTokenRepository passwordResetTokenRepository;
  public PasswordRestTokenService(PasswordResetTokenRepository passwordResetTokenRepository) {
    this.passwordResetTokenRepository = passwordResetTokenRepository;
  }

  public void createPasswordRestToken(PasswordResetToken passwordResetToken){
    Optional<PasswordResetToken> existingPasswordResetToken = passwordResetTokenRepository.findById(passwordResetToken.getId());

    if (existingPasswordResetToken.isPresent()) {
      throw new EntityExistsException("The password reset token with the same ID already exists.");
    }

    passwordResetTokenRepository.save(passwordResetToken);
  }
  public Optional<PasswordResetToken> findByToken(String token){
    return passwordResetTokenRepository.findByToken(token);
  }
  public void markAsUsed(PasswordResetToken passwordResetToken){
    passwordResetToken.markAsUsed();
    passwordResetTokenRepository.save(passwordResetToken);
  }
}
