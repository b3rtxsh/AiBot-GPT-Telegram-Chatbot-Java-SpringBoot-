package com.telegram.bot.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    public boolean isAllowed(String key, int maxRequests, int windowSeconds) {
        String redisKey = "rate_limit:" + key;
        long now = Instant.now().getEpochSecond();

        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, now - windowSeconds);

        redisTemplate.opsForZSet().add(redisKey, String.valueOf(now), now);

        Long count = redisTemplate.opsForZSet().zCard(redisKey);

        redisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds + 1));

        return count != null && count <= maxRequests;
    }
}
