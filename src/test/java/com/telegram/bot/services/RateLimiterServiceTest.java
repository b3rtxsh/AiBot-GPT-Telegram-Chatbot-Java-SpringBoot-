package com.telegram.bot.services;

import com.telegram.bot.StarterApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = StarterApplication.class)
@Testcontainers
@ActiveProfiles("test")
class RateLimiterServiceTest {

    // 🧱 Поднимаем Redis в Testcontainers
    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7.0.5").withExposedPorts(6379);

    // ⚙️ Передаём Spring-у актуальные host/port из контейнера
    @DynamicPropertySource
    static void configureRedisProperties(DynamicPropertyRegistry registry) {
        redis.start(); // важно: стартуем до загрузки контекста
        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379).toString());
    }

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RateLimiterService rateLimiterService;

    @AfterEach
    void cleanup() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    @Test
    void shouldAllowRequestsWithinLimit() {
        for (int i = 0; i < 5; i++) {
            boolean allowed = rateLimiterService.isAllowed("user:1", 5, 60);
            assertThat(allowed).isTrue();
        }
    }

    @Test
    void shouldBlockWhenLimitExceeded() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            rateLimiterService.isAllowed("user:2", 5, 60);
            Thread.sleep(1100);
        }
        boolean sixth = rateLimiterService.isAllowed("user:2", 5, 60);
        assertThat(sixth).isFalse();
    }

    @Test
    void shouldAllowAfterWindowExpires() throws InterruptedException {
        rateLimiterService.isAllowed("user:3", 1, 1);
        Thread.sleep(1100);
        boolean allowed = rateLimiterService.isAllowed("user:3", 1, 1);
        assertThat(allowed).isTrue();
    }
}
