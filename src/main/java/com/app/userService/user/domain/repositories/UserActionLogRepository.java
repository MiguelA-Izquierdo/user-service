package com.app.userService.user.domain.repositories;

import com.app.userService.user.domain.model.*;


public interface UserActionLogRepository {
  void save(UserActionLog userActionLog);
//  PaginatedResult<UserActionLog> findAll(int page, int size);
//  PaginatedResult<UserActionLog> findByUser(User user);

}
