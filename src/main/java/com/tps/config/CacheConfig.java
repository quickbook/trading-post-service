package com.tps.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.support.SimpleCacheManager;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.tps.cache.CacheNames;

@Configuration
public class CacheConfig {

    private Caffeine<Object, Object> caffeine(long duration, TimeUnit unit, int maxSize) {
        return Caffeine.newBuilder()
                .initialCapacity(200)
                .maximumSize(maxSize)
                .expireAfterWrite(duration, unit)
                .recordStats();
    }

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();

        List<Cache> caches = new ArrayList<>();

        // ---------------------
        //  FIRMS (24 HOURS)
        // ---------------------
        caches.add(new CaffeineCache(CacheNames.FIRMS_BY_ID,
                caffeine(24, TimeUnit.HOURS, 10000).build()));
        caches.add(new CaffeineCache(CacheNames.FIRMS_LIST,
                caffeine(24, TimeUnit.HOURS, 10000).build()));
        caches.add(new CaffeineCache(CacheNames.FIRMS_FIND_LIST,
                caffeine(24, TimeUnit.HOURS, 10000).build()));
        caches.add(new CaffeineCache(CacheNames.FIRMS_LITE,
                caffeine(24, TimeUnit.HOURS, 10000).build()));

        // ---------------------
        //  CHALLENGES (24 HOURS)
        // ---------------------
        caches.add(new CaffeineCache(CacheNames.CHALLENGES_BY_FIRM,
                caffeine(24, TimeUnit.HOURS, 5000).build()));
        caches.add(new CaffeineCache(CacheNames.CHALLENGES_BY_ID,
                caffeine(24, TimeUnit.HOURS, 5000).build()));
        caches.add(new CaffeineCache(CacheNames.CHALLENGES_ALL,
                caffeine(24, TimeUnit.HOURS, 5000).build()));

        // ---------------------
        //  REVIEWS (SHORTER TTL)
        // ---------------------
        caches.add(new CaffeineCache(CacheNames.REVIEWS_BY_FIRM,
                caffeine(30, TimeUnit.MINUTES, 2000).build()));
        caches.add(new CaffeineCache(CacheNames.REVIEWS_BY_ID,
                caffeine(1, TimeUnit.HOURS, 2000).build()));
        caches.add(new CaffeineCache(CacheNames.REVIEWS_ALL,
                caffeine(30, TimeUnit.MINUTES, 2000).build()));

        // ---------------------
        //  DOMAIN LOOKUPS (24 HOURS)
        // ---------------------
        caches.add(new CaffeineCache(CacheNames.COUNTRIES,
                caffeine(24, TimeUnit.HOURS, 1000).build()));
        caches.add(new CaffeineCache(CacheNames.TRADING_PLATFORMS,
                caffeine(24, TimeUnit.HOURS, 1000).build()));
        caches.add(new CaffeineCache(CacheNames.INSTRUMENTS,
                caffeine(24, TimeUnit.HOURS, 1000).build()));
        caches.add(new CaffeineCache(CacheNames.TIERS,
                caffeine(24, TimeUnit.HOURS, 1000).build()));
        caches.add(new CaffeineCache(CacheNames.CHALLENGE_PHASES,
                caffeine(24, TimeUnit.HOURS, 1000).build()));
        caches.add(new CaffeineCache(CacheNames.DRAWDOWN_TYPES,
                caffeine(24, TimeUnit.HOURS, 1000).build()));
        caches.add(new CaffeineCache(CacheNames.PAYOUT_FREQUENCIES,
                caffeine(24, TimeUnit.HOURS, 1000).build()));
        caches.add(new CaffeineCache(CacheNames.CURRENCIES,
                caffeine(24, TimeUnit.HOURS, 1000).build()));
        caches.add(new CaffeineCache(CacheNames.ROLES,
                caffeine(24, TimeUnit.HOURS, 1000).build()));

        manager.setCaches(caches);
        return manager;
    }
}
