package com.suryadeep.openshop.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuration class for caching in the application.
 * Uses Caffeine as the caching provider.
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    @Value("${caffeine.maximum-size}")
    private int maximumSize;
    @Value("${caffeine.expire-after-access}")
    private Duration expireAfterAccess;

    /**
     * Creates and configures the Caffeine cache manager.
     * 
     * @return the configured cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        log.info("Initializing Caffeine Cache Manager");
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        
        // Configure Caffeine
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterAccess(expireAfterAccess)
                .recordStats();
        
        cacheManager.setCaffeine(caffeine);
        

        cacheManager.setCacheNames(java.util.Arrays.asList(
                "categories", "products", "users", "orders"
        ));
        
        log.info("Caffeine Cache Manager initialized successfully");
        return cacheManager;
    }
}