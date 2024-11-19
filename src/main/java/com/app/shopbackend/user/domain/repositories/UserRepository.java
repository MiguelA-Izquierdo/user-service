package com.app.shopbackend.user.domain.repositories;

import com.app.shopbackend.user.domain.model.User;
import com.app.shopbackend.user.domain.valueObjects.UserId;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
  void save(User user);
  Optional<User> findById(UserId id);
  List<User> findAll();
  void update(User client);
  void deleteById(UserId id);
}
