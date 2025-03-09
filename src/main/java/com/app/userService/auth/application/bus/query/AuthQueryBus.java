package com.app.userService.auth.application.bus.query;

import com.app.userService._shared.application.bus.query.Query;
import com.app.userService._shared.application.bus.query.QueryBus;
import com.app.userService._shared.application.bus.query.QueryHandler;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class AuthQueryBus implements QueryBus {

  private final Map<Class<? extends Query<?>>, QueryHandler<?, ?>> handlers = new HashMap<>();

  public AuthQueryBus(Set<QueryHandler<?, ?>> queryHandlers) {
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
      throw new RuntimeException("No handler auth found for query: " + query.getClass().getName());
    }
    return handler.handle(query);
  }
}

