package com.app.userService._shared.infrastructure.ratelimit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class InMemoryRateLimitBackend implements RateLimitBackend {

    // Evicts buckets not accessed in 1h; caps at 50k IPs to bound memory during attacks
    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
        .expireAfterAccess(1, TimeUnit.HOURS)
        .maximumSize(50_000)
        .build();

    @Override
    public boolean tryConsume(String key, long maxTokens, Duration window) {
        Bucket bucket = cache.get(key, k -> Bucket.builder()
            .addLimit(Bandwidth.builder()
                .capacity(maxTokens)
                .refillIntervally(maxTokens, window)
                .build())
            .build());
        return bucket.tryConsume(1);
    }

    // Exposed for integration tests to reset state between test methods
    public void clearAll() {
        cache.invalidateAll();
    }
}