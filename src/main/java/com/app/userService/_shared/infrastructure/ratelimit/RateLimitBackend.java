package com.app.userService._shared.infrastructure.ratelimit;

import java.time.Duration;

public interface RateLimitBackend {
    boolean tryConsume(String key, long maxTokens, Duration window);
}