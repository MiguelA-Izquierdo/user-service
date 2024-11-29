package com.app.shopbackend.user.domain.repositories;

import com.app.shopbackend.user.domain.model.User;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
  void save(User user);
  List<User> findAll(int page, int size);
  Optional<User> findByEmail(String email);
  Optional<User> findByIdOrEmail(@Param("userId") UUID userId, @Param("email") String email);

  Optional<User> findById(UUID id);

}
