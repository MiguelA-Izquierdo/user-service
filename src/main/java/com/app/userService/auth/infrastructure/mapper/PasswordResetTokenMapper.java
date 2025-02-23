package com.app.userService.auth.infrastructure.mapper;

import com.app.userService.auth.domain.model.PasswordResetToken;
import com.app.userService.auth.infrastructure.entities.PasswordResetTokenEntity;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.infrastructure.entities.UserEntity;
import com.app.userService.user.infrastructure.mapper.UserMapper;

public class PasswordResetTokenMapper {

  public static PasswordResetTokenEntity toEntity(PasswordResetToken passwordResetToken) {
    UserEntity userEntity = UserMapper.toEntity(passwordResetToken.getUser());
    return new PasswordResetTokenEntity(
      passwordResetToken.getId(),
      userEntity,
      passwordResetToken.getToken(),
      passwordResetToken.getExpirationDate(),
      passwordResetToken.isUsed()
    );
  }

  public static PasswordResetToken toDomain(PasswordResetTokenEntity entity) {
    User user = UserMapper.toDomain(entity.getUser());
    return PasswordResetToken.of(
      entity.getId(),
      user,
      entity.getToken(),
      entity.getExpirationDate(),
      entity.isUsed()
      );
  }
}
