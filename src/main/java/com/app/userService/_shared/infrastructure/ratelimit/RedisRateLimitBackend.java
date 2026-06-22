package com.app.userService._shared.infrastructure.ratelimit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.time.Duration;
import java.util.List;

public class RedisRateLimitBackend implements RateLimitBackend {

    private static final Logger logger = LoggerFactory.getLogger(RedisRateLimitBackend.class);

    // Atomic: increment counter; set TTL only on the first request of each window
    private static final RedisScript<Long> INCR_WITH_EXPIRE = RedisScript.of(
        "local c = redis.call('INCR', KEYS[1]) " +
        "if c == 1 then redis.call('EXPIRE', KEYS[1], ARGV[1]) end " +
        "return c",
        Long.class
    );

    private final StringRedisTemplate redis;

    public RedisRateLimitBackend(StringRedisTemplate redis) {
        this.redis = redis;
    }

    @Override
    public boolean tryConsume(String key, long maxTokens, Duration window) {
        try {
            return tryConsumeOrThrow(key, maxTokens, window);
        } catch (Exception e) {
            // Fail open: Redis down should not block legitimate users
            logger.error("Redis unavailable for rate limiting, failing open. key={}", key, e);
            return true;
        }
    }

    // Propagates Redis failures so a caller (e.g. ResilientRateLimitBackend) can decide
    // how to degrade instead of silently failing open.
    public boolean tryConsumeOrThrow(String key, long maxTokens, Duration window) {
        Long count = redis.execute(INCR_WITH_EXPIRE,
            List.of("rl:" + key),
            String.valueOf(window.getSeconds()));
        return count != null && count <= maxTokens;
    }
}