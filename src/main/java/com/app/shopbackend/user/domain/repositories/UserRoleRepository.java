package com.app.shopbackend.user.domain.repositories;

import com.app.shopbackend.user.domain.model.Role;
import com.app.shopbackend.user.domain.model.User;

import java.util.Optional;

public interface UserRoleRepository {
  void save(User user, Role role);
  Optional<Role> findRoleByUserIdRoleName(User user, Role role);
}
