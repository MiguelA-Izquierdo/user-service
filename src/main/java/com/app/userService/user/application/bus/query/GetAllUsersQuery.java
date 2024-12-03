package com.app.userService.user.application.bus.query;


import com.app.userService._shared.bus.query.Query;
import com.app.userService.user.domain.model.PaginatedResult;
import com.app.userService.user.domain.model.User;

import java.util.List;

public record GetAllUsersQuery(Integer  page, Integer  size) implements Query<PaginatedResult<User>> {
}
