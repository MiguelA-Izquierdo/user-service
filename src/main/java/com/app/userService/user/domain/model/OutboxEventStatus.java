package com.app.userService.user.domain.model;

public enum OutboxEventStatus {
  // Ready to be published: either never attempted (initial state) or due for a retry.
  PENDING,
  // Published successfully; terminal.
  PROCESSED,
  // Transient publish failure; will be retried once nextRetryAt has elapsed.
  FAILED,
  // Retry budget exhausted; the poller no longer picks it up. Terminal, needs manual intervention.
  DEAD
}
