package com.tps.cache.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.event.TransactionPhase;

import com.tps.cache.CacheNames;

@Component
public class CacheInvalidationListener {

    private static final Logger log = LoggerFactory.getLogger(CacheInvalidationListener.class);

    private final CacheManager cacheManager;

    public CacheInvalidationListener(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * Invoked AFTER transaction commit to ensure we only evict if the DB write succeeded.
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onFirmChanged(FirmChangedEvent event) {
        Long id = event.getFirmId();
        log.debug("Received FirmChangedEvent for id={}", id);

        try {
            // Firms caches
            evictCacheByKey(CacheNames.FIRMS_BY_ID, id);
            clearCache(CacheNames.FIRMS_LIST);
            clearCache(CacheNames.FIRMS_FIND_LIST);
            clearCache(CacheNames.FIRMS_LITE);

            // Challenges caches
            evictCacheByKey(CacheNames.CHALLENGES_BY_FIRM, id);
            clearCache(CacheNames.CHALLENGES_ALL);
            // We don't know a specific challengeId here, so clear the per-id cache to be safe
            clearCache(CacheNames.CHALLENGES_BY_ID);

            // Reviews caches
            evictCacheByKey(CacheNames.REVIEWS_BY_FIRM, id);
            clearCache(CacheNames.REVIEWS_ALL);
            // We don't have reviewId in FirmChangedEvent — clearing review-by-id cache is safer than evicting with firmId
            clearCache(CacheNames.REVIEWS_BY_ID);

            // Add more domain caches here as needed (payouts, leverages, etc.)
        } catch (Exception ex) {
            // Do not rethrow — invalidation should not break the main flow
            log.error("Error during cache invalidation for firm id=" + id, ex);
        }
    }

    // --- Helper utilities ---

    private void clearCache(String cacheName) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.debug("Cleared cache {}", cacheName);
            } else {
                log.trace("Cache '{}' not found (clear skipped)", cacheName);
            }
        } catch (Exception ex) {
            log.warn("Failed to clear cache '{}': {}", cacheName, ex.getMessage(), ex);
        }
    }

    private void evictCacheByKey(String cacheName, Object key) {
        if (key == null) {
            log.trace("Key is null; skipping evict for cache '{}'", cacheName);
            return;
        }
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.evict(key);
                log.debug("Evicted {}[{}]", cacheName, key);
            } else {
                log.trace("Cache '{}' not found (evict skipped)", cacheName);
            }
        } catch (Exception ex) {
            log.warn("Failed to evict key '{}' from cache '{}': {}", key, cacheName, ex.getMessage(), ex);
        }
    }
}
