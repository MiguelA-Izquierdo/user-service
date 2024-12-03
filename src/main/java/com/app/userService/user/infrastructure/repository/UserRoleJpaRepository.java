package com.app.userService.user.infrastructure.repository;

import com.app.userService.user.domain.model.Role;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.infrastructure.entities.UserEntity;
import com.app.userService.user.infrastructure.entities.UserRoleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRoleJpaRepository extends org.springframework.data.jpa.repository.JpaRepository<UserRoleEntity, UUID>{
  @Query("SELECT ur FROM UserRoleEntity ur WHERE ur.user.id = :userId AND ur.role = :role")
  List<UserRoleEntity> findByUserIdRoleName(@Param("userId") UUID userId, @Param("role") Role role);
  void deleteByUser(UserEntity userEntity);
}
