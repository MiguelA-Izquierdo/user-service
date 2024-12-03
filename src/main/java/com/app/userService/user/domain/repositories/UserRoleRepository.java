package com.app.userService.user.domain.repositories;

import com.app.userService.user.domain.model.Role;
import com.app.userService.user.domain.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRoleRepository {
  void save(User user, Role role);
  void deleteByUser(User user);
  Optional<Role> findRoleByUserIdRoleName(User user, Role role);
}
