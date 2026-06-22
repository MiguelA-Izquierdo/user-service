package units.userService._shared.infrastructure.ratelimit;

import com.app.userService._shared.infrastructure.ratelimit.InMemoryRateLimitBackend;
import com.app.userService._shared.infrastructure.ratelimit.RedisRateLimitBackend;
import com.app.userService._shared.infrastructure.ratelimit.ResilientRateLimitBackend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ResilientRateLimitBackendTest {

    private static final Duration WINDOW = Duration.ofMinutes(1);

    private RedisRateLimitBackend redis;
    private InMemoryRateLimitBackend fallback;
    private ResilientRateLimitBackend backend;

    @BeforeEach
    void setUp() {
        redis = mock(RedisRateLimitBackend.class);
        fallback = new InMemoryRateLimitBackend();
        backend = new ResilientRateLimitBackend(redis, fallback);
    }

    @Test
    void whenRedisAllows_returnsTrue_andDoesNotTouchFallback() {
        InMemoryRateLimitBackend spyFallback = mock(InMemoryRateLimitBackend.class);
        ResilientRateLimitBackend resilient = new ResilientRateLimitBackend(redis, spyFallback);
        when(redis.tryConsumeOrThrow(anyString(), anyLong(), any())).thenReturn(true);

        assertTrue(resilient.tryConsume("key", 5, WINDOW));
        verifyNoInteractions(spyFallback);
    }

    @Test
    void whenRedisBlocks_returnsFalse_withoutFallingBack() {
        InMemoryRateLimitBackend spyFallback = mock(InMemoryRateLimitBackend.class);
        ResilientRateLimitBackend resilient = new ResilientRateLimitBackend(redis, spyFallback);
        when(redis.tryConsumeOrThrow(anyString(), anyLong(), any())).thenReturn(false);

        assertFalse(resilient.tryConsume("key", 5, WINDOW));
        verifyNoInteractions(spyFallback);
    }

    @Test
    void whenRedisThrows_degradesToInMemoryLimit() {
        when(redis.tryConsumeOrThrow(anyString(), anyLong(), any()))
            .thenThrow(new RuntimeException("Redis down"));

        // Fallback still enforces a real limit instead of failing open
        for (int i = 0; i < 5; i++) {
            assertTrue(backend.tryConsume("degraded-key", 5, WINDOW), "Request " + (i + 1) + " should pass");
        }
        assertFalse(backend.tryConsume("degraded-key", 5, WINDOW), "6th request should be blocked by fallback");
    }
}