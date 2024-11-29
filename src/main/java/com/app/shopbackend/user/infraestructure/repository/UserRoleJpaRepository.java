package com.app.shopbackend.user.infraestructure.repository;

import com.app.shopbackend.user.domain.model.Role;
import com.app.shopbackend.user.infraestructure.entities.UserRoleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface UserRoleJpaRepository extends org.springframework.data.jpa.repository.JpaRepository<UserRoleEntity, UUID>{
  @Query("SELECT ur FROM UserRoleEntity ur WHERE ur.user.id = :userId AND ur.role = :role")
  List<UserRoleEntity> findByUserIdRoleName(@Param("userId") UUID userId, @Param("role") Role role);

}
