package com.app.userService._shared.security;

import com.app.userService._shared.infrastructure.ratelimit.RateLimitBackend;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    private final RateLimitBackend backend;
    // Only trust X-Forwarded-For when a reverse proxy/load balancer sets it (avoids IP spoofing)
    private final boolean trustProxy;

    public RateLimitingFilter(RateLimitBackend backend, boolean trustProxy) {
        this.backend = backend;
        this.trustProxy = trustProxy;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        RateLimitRule rule = resolveRule(request.getRequestURI(), request.getMethod());
        if (rule == null) {
            chain.doFilter(request, response);
            return;
        }

        String ip = extractClientIp(request);
        String key = rule.key() + "|" + ip;

        if (backend.tryConsume(key, rule.maxTokens(), rule.window())) {
            chain.doFilter(request, response);
        } else {
            logger.warn("Rate limit exceeded ip={} endpoint={}", ip, rule.key());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json");
            response.setHeader("Retry-After", String.valueOf(rule.window().getSeconds()));
            response.getWriter().write(
                "{\"status\":\"error\",\"code\":429,\"message\":\"Too many requests. Please try again later.\"}"
            );
        }
    }

    private RateLimitRule resolveRule(String path, String method) {
        if ("POST".equalsIgnoreCase(method)) {
            if ("/auth".equals(path))                        return new RateLimitRule("login",  6, Duration.ofMinutes(1));
            if ("/auth/unlock-reset-password".equals(path)) return new RateLimitRule("unlock", 3, Duration.ofMinutes(5));
        }
        return null;
    }

    private String extractClientIp(HttpServletRequest request) {
        if (trustProxy) {
            String forwarded = request.getHeader("X-Forwarded-For");
            if (forwarded != null && !forwarded.isBlank()) {
                return forwarded.split(",")[0].trim();
            }
        }
        return request.getRemoteAddr();
    }

    private record RateLimitRule(String key, long maxTokens, Duration window) {}
}