package com.app.userService.auth.infrastructure.repositories;

import com.app.userService.auth.infrastructure.entities.PasswordResetTokenEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenJpaRepository extends org.springframework.data.jpa.repository.JpaRepository<PasswordResetTokenEntity, UUID>{
  Optional<PasswordResetTokenEntity> findByToken(String token);
  Optional<PasswordResetTokenEntity> findByUserId(UUID userId);
  List<PasswordResetTokenEntity> findAllByUserId(UUID userId);
  boolean existsByUserIdAndIsUsedFalse(UUID userId);
}
