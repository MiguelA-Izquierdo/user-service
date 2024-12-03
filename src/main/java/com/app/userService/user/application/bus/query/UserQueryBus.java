package com.app.userService.user.application.bus.query;

import com.app.userService._shared.bus.query.Query;
import com.app.userService._shared.bus.query.QueryBus;
import com.app.userService._shared.bus.query.QueryHandler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class UserQueryBus implements QueryBus {

  private final Map<Class<? extends Query<?>>, QueryHandler<?, ?>> handlers = new HashMap<>();

  public UserQueryBus(Set<QueryHandler<?, ?>> queryHandlers) {
    for (QueryHandler<?, ?> handler : queryHandlers) {
      Class<?> queryType = (Class<?>) ((java.lang.reflect.ParameterizedType) handler.getClass()
        .getGenericInterfaces()[0]).getActualTypeArguments()[0];
      handlers.put((Class<? extends Query<?>>) queryType, handler);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R, Q extends Query<R>> R send(Q query) {
    QueryHandler<Q, R> handler = (QueryHandler<Q, R>) handlers.get(query.getClass());
    if (handler == null) {
      throw new RuntimeException("No handler user found for query: " + query.getClass().getName());
    }
    return handler.handle(query);
  }
}

