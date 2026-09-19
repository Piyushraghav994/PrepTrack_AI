package com.PrepTrack_AI.Fullstack_Project.integration;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test utilizing Testcontainers to verify Redis availability.
 * Disabled by default to prevent build failures on systems without a running Docker daemon.
 */
@Testcontainers
@Disabled("Disabled by default as it requires a local running Docker environment. Enable to run Redis integration tests.")
public class RedisIntegrationTest {

    @Container
    public static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.0-alpine"))
            .withExposedPorts(6379);

    @Test
    void testRedisContainerIsRunning() {
        assertTrue(redis.isRunning());
    }
}
