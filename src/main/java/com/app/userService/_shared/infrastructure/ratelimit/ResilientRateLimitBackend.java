package com.app.userService._shared.infrastructure.ratelimit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

/**
 * Wraps the distributed Redis backend with a local fallback. When Redis is
 * reachable the cluster-wide limit applies; when Redis is unavailable we
 * degrade to a per-instance in-memory limit instead of removing rate limiting
 * altogether (fail-open). This keeps protection on sensitive endpoints (e.g.
 * /auth brute force) alive during a Redis outage.
 */
public class ResilientRateLimitBackend implements RateLimitBackend {

    private static final Logger logger = LoggerFactory.getLogger(ResilientRateLimitBackend.class);

    private final RedisRateLimitBackend redis;
    private final RateLimitBackend fallback;

    public ResilientRateLimitBackend(RedisRateLimitBackend redis, RateLimitBackend fallback) {
        this.redis = redis;
        this.fallback = fallback;
    }

    @Override
    public boolean tryConsume(String key, long maxTokens, Duration window) {
        try {
            return redis.tryConsumeOrThrow(key, maxTokens, window);
        } catch (Exception e) {
            logger.warn("Redis unavailable, degrading to in-memory rate limiting. key={}", key, e);
            return fallback.tryConsume(key, maxTokens, window);
        }
    }
}