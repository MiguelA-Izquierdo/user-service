package com.app.userService.auth.domain.repositories;

import com.app.userService.auth.domain.model.PasswordResetToken;

import java.util.Optional;
import java.util.UUID;


public interface PasswordResetTokenRepository {
  void save(PasswordResetToken passwordResetToken);
  Optional<PasswordResetToken> findByToken(String token);
  Optional<PasswordResetToken> findById(UUID passwordResetTokenId);
}
