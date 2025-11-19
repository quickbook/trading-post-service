package com.tps.cache.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.tps.cache.CacheNames;
import com.tps.enums.EventChangeType;

@Component
public class CacheInvalidationListener {

    private static final Logger log = LoggerFactory.getLogger(CacheInvalidationListener.class);

    private final CacheManager cacheManager;

    public CacheInvalidationListener(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onDomainChanged(DataChangedEvent event) {
        EventChangeType type = event.getType();
        Long firmId = event.getFirmId();
        Long reviewId = event.getReviewId();
        Long challengeId = event.getChallengeId();

        log.info("DomainChangedEvent: {}", type);

        switch (type) {
            /* ========== FIRM ========== */
            case FIRM_CREATED:
                // New firm – refresh firm lists so the new firm appears
                evictFirmCachesForCreate(firmId);
                break;
            case FIRM_UPDATED:
                // Firm changed – evict firm-by-id and refresh lists
                evictFirmCachesForUpdate(firmId);
                break;
            case FIRM_DELETED:
                // Firm removed – evict by id and refresh lists
                evictFirmCachesForDelete(firmId);
                break;

            /* ========== REVIEW ========== */
            case REVIEW_CREATED:
                evictReviewCachesForCreate(firmId, reviewId);
                break;
            case REVIEW_UPDATED:
                evictReviewCachesForUpdate(firmId, reviewId);
                break;
            case REVIEW_DELETED:
                evictReviewCachesForDelete(firmId, reviewId);
                break;

            /* ========== CHALLENGE ========== */
            case CHALLENGE_CREATED:
                evictChallengeCachesForCreate(firmId, challengeId);
                break;
            case CHALLENGE_UPDATED:
                evictChallengeCachesForUpdate(firmId, challengeId);
                break;
            case CHALLENGE_DELETED:
                evictChallengeCachesForDelete(firmId, challengeId);
                break;

            default:
                log.debug("Unhandled change type: {}", type);
        }
    }

    // -------------------- Firm helpers --------------------

    private void evictFirmCachesForCreate(Long firmId) {
        // new firm: refresh list caches and nothing to evict by id yet (or evict if firmId present)
        clear(CacheNames.FIRMS_LIST);
        clear(CacheNames.FIRMS_FIND_LIST);
        clear(CacheNames.FIRMS_LITE);
        if (firmId != null) evict(CacheNames.FIRMS_BY_ID, firmId);
    }

    private void evictFirmCachesForUpdate(Long firmId) {
        // updated firm: evict firm id + refresh lists that show firm summaries
        evict(CacheNames.FIRMS_BY_ID, firmId);
        clear(CacheNames.FIRMS_LIST);
        clear(CacheNames.FIRMS_FIND_LIST);
        clear(CacheNames.FIRMS_LITE);
    }

    private void evictFirmCachesForDelete(Long firmId) {
        // deleted firm: evict id and refresh lists
        evict(CacheNames.FIRMS_BY_ID, firmId);
        clear(CacheNames.FIRMS_LIST);
        clear(CacheNames.FIRMS_FIND_LIST);
        clear(CacheNames.FIRMS_LITE);
    }

    // -------------------- Review helpers --------------------

    private void evictReviewCachesForCreate(Long firmId, Long reviewId) {
        // new review: evict per-firm review list and optionally all-reviews listing
        evict(CacheNames.REVIEWS_BY_FIRM, firmId);
        // if you keep a global reviewsAll list, refresh it
        clear(CacheNames.REVIEWS_ALL);
        if (reviewId != null) evict(CacheNames.REVIEWS_BY_ID, reviewId);
        
        if (firmId != null) evict(CacheNames.FIRMS_BY_ID, firmId);
        
    }

    private void evictReviewCachesForUpdate(Long firmId, Long reviewId) {
        // updated review: evict the review id and the firm's review list
        evict(CacheNames.REVIEWS_BY_ID, reviewId);
        evict(CacheNames.REVIEWS_BY_FIRM, firmId);
        clear(CacheNames.REVIEWS_ALL);
        if (firmId != null) evict(CacheNames.FIRMS_BY_ID, firmId);
    }

    private void evictReviewCachesForDelete(Long firmId, Long reviewId) {
        evict(CacheNames.REVIEWS_BY_ID, reviewId);
        evict(CacheNames.REVIEWS_BY_FIRM, firmId);
        clear(CacheNames.REVIEWS_ALL);
        if (firmId != null) evict(CacheNames.FIRMS_BY_ID, firmId);
    }

    // -------------------- Challenge helpers --------------------

    private void evictChallengeCachesForCreate(Long firmId, Long challengeId) {
        evict(CacheNames.CHALLENGES_BY_FIRM, firmId);
        clear(CacheNames.CHALLENGES_ALL);
        if (challengeId != null) evict(CacheNames.CHALLENGES_BY_ID, challengeId);
        if (firmId != null) evict(CacheNames.FIRMS_BY_ID, firmId);
    }

    private void evictChallengeCachesForUpdate(Long firmId, Long challengeId) {
        evict(CacheNames.CHALLENGES_BY_ID, challengeId);
        evict(CacheNames.CHALLENGES_BY_FIRM, firmId);
        clear(CacheNames.CHALLENGES_ALL);
        if (firmId != null) evict(CacheNames.FIRMS_BY_ID, firmId);
    }

    private void evictChallengeCachesForDelete(Long firmId, Long challengeId) {
        evict(CacheNames.CHALLENGES_BY_ID, challengeId);
        evict(CacheNames.CHALLENGES_BY_FIRM, firmId);
        clear(CacheNames.CHALLENGES_ALL);
        if (firmId != null) evict(CacheNames.FIRMS_BY_ID, firmId);
    }

    // -------------------- Generic cache ops --------------------

    private void clear(String cacheName) {
        try {
            Cache c = cacheManager.getCache(cacheName);
            if (c != null) {
                c.clear();
                log.debug("Cleared cache {}", cacheName);
            } else {
                log.trace("Cache '{}' not found (clear skipped)", cacheName);
            }
        } catch (Exception ex) {
            log.warn("Failed to clear cache '{}': {}", cacheName, ex.getMessage(), ex);
        }
    }

    private void evict(String cacheName, Object key) {
        if (key == null) {
            log.trace("Key is null; skipping evict for cache '{}'", cacheName);
            return;
        }
        try {
            Cache c = cacheManager.getCache(cacheName);
            if (c != null) {
                c.evict(key);
                log.debug("Evicted {}[{}]", cacheName, key);
            } else {
                log.trace("Cache '{}' not found (evict skipped)", cacheName);
            }
        } catch (Exception ex) {
            log.warn("Failed to evict key '{}' from cache '{}': {}", key, cacheName, ex.getMessage(), ex);
        }
    }
}
