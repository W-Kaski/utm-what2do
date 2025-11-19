package com.utm.what2do.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine 本地缓存配置
 */
@Configuration
@EnableCaching
public class CaffeineCacheConfig {

    /**
     * 配置缓存管理器（默认）
     */
    @Bean
    @Primary
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 设置最大缓存数量
                .maximumSize(1000)
                // 设置缓存过期时间（写入后）
                .expireAfterWrite(10, TimeUnit.MINUTES)
                // 设置缓存过期时间（访问后）
                .expireAfterAccess(30, TimeUnit.MINUTES)
                // 初始化缓存空间大小
                .initialCapacity(100)
                // 开启缓存统计
                .recordStats());
        return cacheManager;
    }

    /**
     * 建筑信息专用缓存（长期有效）
     */
    @Bean("buildingCache")
    public CacheManager buildingCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("buildings");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)
                // 建筑信息是静态数据，缓存时间较长
                .expireAfterWrite(24, TimeUnit.HOURS)
                .initialCapacity(50)
                .recordStats());
        return cacheManager;
    }
}
