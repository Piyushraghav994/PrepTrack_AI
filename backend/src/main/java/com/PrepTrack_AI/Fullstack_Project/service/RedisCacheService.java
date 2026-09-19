package com.PrepTrack_AI.Fullstack_Project.service;

import java.time.Duration;

/**
 * Service interface for general caching operations using Redis.
 */
public interface RedisCacheService {

    void set(String key, Object value, Duration ttl);

    Object get(String key);

    boolean hasKey(String key);

    void delete(String key);
}
