package com.app.userService.auth.infrastructure.repositories;

import com.app.userService.auth.domain.model.PasswordResetToken;
import com.app.userService.auth.domain.repositories.PasswordResetTokenRepository;
import com.app.userService.auth.infrastructure.entities.PasswordResetTokenEntity;
import com.app.userService.auth.infrastructure.mapper.PasswordResetTokenMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PasswordResetTokenRepositorySql implements PasswordResetTokenRepository {
  private final PasswordResetTokenJpaRepository jpaRepository;
  public PasswordResetTokenRepositorySql(PasswordResetTokenJpaRepository jpaRepository){
    this.jpaRepository = jpaRepository;
  }
  @Override
  public void save(PasswordResetToken passwordResetToken){
    PasswordResetTokenEntity passwordResetTokenEntity = PasswordResetTokenMapper.toEntity(passwordResetToken);
    jpaRepository.save(passwordResetTokenEntity);
  }

  @Override
  public Optional<PasswordResetToken> findByToken(String token){
    return jpaRepository.findByToken(token)
      .map(PasswordResetTokenMapper::toDomain);
  }
  @Override
  public Optional<PasswordResetToken> findById(UUID id){
    return jpaRepository.findById(id)
      .map(PasswordResetTokenMapper::toDomain);
  }
}
