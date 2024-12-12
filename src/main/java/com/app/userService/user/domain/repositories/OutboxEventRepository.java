package com.app.userService.user.domain.repositories;

import com.app.userService.user.domain.model.*;

import java.util.List;

public interface OutboxEventRepository {
  void save(OutboxEvent outboxEvent);
  List<OutboxEvent> findByStatus(OutboxEventStatus status);
  List<OutboxEvent> findByQueue(String queue);
  List<OutboxEvent> findByRoutingKey(String routingKey);

}
