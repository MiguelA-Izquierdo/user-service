package units.userService._shared.infrastructure.ratelimit;

import com.app.userService._shared.infrastructure.ratelimit.InMemoryRateLimitBackend;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryRateLimitBackendTest {

    private InMemoryRateLimitBackend backend;

    @BeforeEach
    void setUp() {
        backend = new InMemoryRateLimitBackend();
    }

    @Test
    void tryConsume_firstRequest_isAllowed() {
        assertTrue(backend.tryConsume("key1", 5, Duration.ofMinutes(1)));
    }

    @Test
    void tryConsume_requestsUpToLimit_areAllowed() {
        for (int i = 0; i < 5; i++) {
            assertTrue(backend.tryConsume("key2", 5, Duration.ofMinutes(1)), "Request " + (i + 1) + " should be allowed");
        }
    }

    @Test
    void tryConsume_requestBeyondLimit_isBlocked() {
        for (int i = 0; i < 5; i++) {
            backend.tryConsume("key3", 5, Duration.ofMinutes(1));
        }
        assertFalse(backend.tryConsume("key3", 5, Duration.ofMinutes(1)));
    }

    @Test
    void tryConsume_differentKeys_haveSeparateBuckets() {
        for (int i = 0; i < 5; i++) {
            backend.tryConsume("exhausted-key", 5, Duration.ofMinutes(1));
        }
        assertTrue(backend.tryConsume("fresh-key", 5, Duration.ofMinutes(1)));
    }

    @Test
    void clearAll_resetsExhaustedBucket() {
        for (int i = 0; i < 5; i++) {
            backend.tryConsume("key4", 5, Duration.ofMinutes(1));
        }
        assertFalse(backend.tryConsume("key4", 5, Duration.ofMinutes(1)));

        backend.clearAll();

        assertTrue(backend.tryConsume("key4", 5, Duration.ofMinutes(1)));
    }
}