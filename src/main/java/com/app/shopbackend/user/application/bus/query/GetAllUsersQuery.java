package com.app.shopbackend.user.application.bus.query;


import com.app.shopbackend._shared.bus.query.Query;
import com.app.shopbackend.user.domain.model.User;

import java.util.List;

public record GetAllUsersQuery(Integer  page, Integer  size) implements Query<List<User>> {
}
