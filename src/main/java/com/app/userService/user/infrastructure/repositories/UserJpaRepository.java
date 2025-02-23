package com.app.userService.user.infrastructure.repositories;

import com.app.userService.user.domain.model.UserStatus;
import com.app.userService.user.infrastructure.entities.UserEntity;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.domain.Pageable;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface UserJpaRepository extends org.springframework.data.jpa.repository.JpaRepository<UserEntity, UUID>{
  Optional<UserEntity> findByEmail(String email);
  @NotNull
  Page<UserEntity> findAllByStatusIn(@NotNull Set<UserStatus> statuses, @NotNull Pageable pageable);
  @Query("SELECT u FROM UserEntity u WHERE u.id = :userId OR u.email = :email")
  Optional<UserEntity> findByIdOrEmail(UUID userId, String email);
}
