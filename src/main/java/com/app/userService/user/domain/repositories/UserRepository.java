package com.app.userService.user.domain.repositories;

import com.app.userService.user.domain.model.PaginatedResult;
import com.app.userService.user.domain.model.User;
import com.app.userService.user.domain.model.AnonymousUser;
import com.app.userService.user.domain.model.UserWrapper;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
  void save(User user);
  PaginatedResult<User> findAll(int page, int size);
  Optional<User> findByEmail(String email);
  UserWrapper findByIdOrEmail(@Param("userId") UUID userId, @Param("email") String email);
  UserWrapper findById(UUID id);
  void anonymize(AnonymousUser anonymousUser);

}
