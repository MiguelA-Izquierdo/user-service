package com.app.userService._shared.infrastructure.ratelimit;

import com.app.userService._shared.security.RateLimitingFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RateLimitAutoConfiguration {

    @Value("${rate-limit.trust-proxy:false}")
    private boolean trustProxy;

    @Bean
    @ConditionalOnProperty(name = "rate-limit.backend", havingValue = "memory", matchIfMissing = true)
    public RateLimitBackend inMemoryRateLimitBackend() {
        return new InMemoryRateLimitBackend();
    }

    @Bean
    @ConditionalOnProperty(name = "rate-limit.backend", havingValue = "redis")
    public RateLimitBackend redisRateLimitBackend(StringRedisTemplate redis) {
        // Distributed limit with a per-instance fallback when Redis is unreachable
        return new ResilientRateLimitBackend(new RedisRateLimitBackend(redis), new InMemoryRateLimitBackend());
    }

    @Bean
    public FilterRegistrationBean<RateLimitingFilter> rateLimitingFilterRegistration(RateLimitBackend backend) {
        FilterRegistrationBean<RateLimitingFilter> reg =
            new FilterRegistrationBean<>(new RateLimitingFilter(backend, trustProxy));
        reg.addUrlPatterns("/*");
        reg.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return reg;
    }
}