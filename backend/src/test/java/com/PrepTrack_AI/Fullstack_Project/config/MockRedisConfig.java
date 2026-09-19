package com.PrepTrack_AI.Fullstack_Project.config;

import com.PrepTrack_AI.Fullstack_Project.service.RedisCacheService;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Mock Redis Configuration for testing.
 * Prevents Spring Boot from attempting to connect to a live Redis server during tests.
 */
@Configuration
public class MockRedisConfig {

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        return Mockito.mock(RedisConnectionFactory.class);
    }

    @Bean
    @Primary
    @SuppressWarnings("unchecked")
    public RedisTemplate<String, Object> redisTemplate() {
        return Mockito.mock(RedisTemplate.class);
    }

    @Bean
    @Primary
    public RedisCacheService redisCacheService() {
        return Mockito.mock(RedisCacheService.class);
    }
}
