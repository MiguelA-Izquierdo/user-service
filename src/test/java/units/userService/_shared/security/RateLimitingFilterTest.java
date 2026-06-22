package units.userService._shared.security;

import com.app.userService._shared.infrastructure.ratelimit.RateLimitBackend;
import com.app.userService._shared.security.RateLimitingFilter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class RateLimitingFilterTest {

    @Mock private RateLimitBackend backend;
    @Mock private FilterChain chain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void doFilter_backendAllows_continuesChain() throws Exception {
        when(backend.tryConsume(anyString(), anyLong(), any(Duration.class))).thenReturn(true);
        RateLimitingFilter filter = new RateLimitingFilter(backend, false);

        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/auth");
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
        assertEquals(200, res.getStatus());
    }

    @Test
    void doFilter_backendBlocks_returns429WithRetryAfterHeader() throws Exception {
        when(backend.tryConsume(anyString(), anyLong(), any(Duration.class))).thenReturn(false);
        RateLimitingFilter filter = new RateLimitingFilter(backend, false);

        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/auth");
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, chain);

        verify(chain, never()).doFilter(any(), any());
        assertEquals(429, res.getStatus());
        assertNotNull(res.getHeader("Retry-After"));
        assertTrue(res.getContentAsString().contains("429"));
    }

    @Test
    void doFilter_noMatchingRule_skipsBackendAndContinues() throws Exception {
        RateLimitingFilter filter = new RateLimitingFilter(backend, false);

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/users/123");
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
        verifyNoInteractions(backend);
    }

    @Test
    void doFilter_getOnAuth_noRuleApplied() throws Exception {
        RateLimitingFilter filter = new RateLimitingFilter(backend, false);

        MockHttpServletRequest req = new MockHttpServletRequest("GET", "/auth");
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, chain);

        verify(chain).doFilter(req, res);
        verifyNoInteractions(backend);
    }

    @Test
    void doFilter_trustProxyTrue_usesFirstIpFromForwardedHeader() throws Exception {
        when(backend.tryConsume(anyString(), anyLong(), any(Duration.class))).thenReturn(false);
        RateLimitingFilter filter = new RateLimitingFilter(backend, true);

        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/auth");
        req.addHeader("X-Forwarded-For", "1.2.3.4, 10.0.0.1");
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, chain);

        verify(backend).tryConsume(contains("1.2.3.4"), anyLong(), any(Duration.class));
    }

    @Test
    void doFilter_trustProxyFalse_ignoresForwardedHeader() throws Exception {
        when(backend.tryConsume(anyString(), anyLong(), any(Duration.class))).thenReturn(false);
        RateLimitingFilter filter = new RateLimitingFilter(backend, false);

        MockHttpServletRequest req = new MockHttpServletRequest("POST", "/auth");
        req.addHeader("X-Forwarded-For", "1.2.3.4");
        MockHttpServletResponse res = new MockHttpServletResponse();

        filter.doFilter(req, res, chain);

        // Must not use the spoofed header IP
        verify(backend).tryConsume(argThat(key -> !key.contains("1.2.3.4")), anyLong(), any(Duration.class));
    }
}