package com.app.userService.user.domain.model;

public enum OutboxEventStatus {
  PROCESSED,
  PENDING,
  FAILED
}
