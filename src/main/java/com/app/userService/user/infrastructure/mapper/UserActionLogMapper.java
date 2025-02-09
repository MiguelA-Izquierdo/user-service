package com.app.userService.user.infrastructure.mapper;


import com.app.userService.user.domain.model.UserActionLog;
import com.app.userService.user.infrastructure.entities.UserActionLogEntity;
import com.app.userService.user.infrastructure.entities.UserEntity;


public class UserActionLogMapper {

  public static UserActionLogEntity toEntity(UserActionLog userActionLog, UserEntity userEntity) {
    return new UserActionLogEntity(
      userActionLog.getId(),
      userEntity,
      userActionLog.getUserAction(),
      userActionLog.getTimestamp(),
      userActionLog.getMetadata()
    );

  }

//  public static UserActionLog toDomain(UserActionLogEntity entity) {
//    return UserActionLog.of(
//      entity.getId(),
//      entity.getUser().getId(),
//      entity.getUserAction(),
//      entity.getTimestamp(),
//      entity.getMetadata()
//    );
//  }
}
