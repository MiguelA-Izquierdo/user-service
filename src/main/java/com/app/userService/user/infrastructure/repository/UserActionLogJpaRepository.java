package com.app.userService.user.infrastructure.repository;

import com.app.userService.user.infrastructure.entities.UserActionLogEntity;

import java.util.UUID;

public interface UserActionLogJpaRepository extends org.springframework.data.jpa.repository.JpaRepository<UserActionLogEntity, UUID>{

}
