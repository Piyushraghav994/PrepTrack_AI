package com.PrepTrack_AI.Fullstack_Project.service.impl;

import com.PrepTrack_AI.Fullstack_Project.service.RedisCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Service implementation of general caching operations using Redis template.
 */
@Service
@RequiredArgsConstructor
public class RedisCacheServiceImpl implements RedisCacheService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RedisCacheServiceImpl.class);
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (Exception e) {
            log.debug("Redis unavailable during set for key {}: {}", key, e.getMessage());
        }
    }

    @Override
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.debug("Redis unavailable during get for key {}: {}", key, e.getMessage());
            return null;
        }
    }

    @Override
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.debug("Redis unavailable during hasKey for key {}: {}", key, e.getMessage());
            return false;
        }
    }

    @Override
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.debug("Redis unavailable during delete for key {}: {}", key, e.getMessage());
        }
    }
}
