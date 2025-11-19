package com.tps.cache.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.tps.enums.EventChangeType;

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
    public void onDomainChanged(DataChangedEvent event) {

        EventChangeType type = event.getType();
        Long firmId = event.getFirmId();
        Long reviewId = event.getReviewId();
        Long challengeId = event.getChallengeId();

        log.info("DomainChangedEvent: {}", event.getType());

        switch (type) {

            /* =======================
               FIRM CHANGES
            ======================== */
            case FIRM_CREATED:
            case FIRM_UPDATED:
            case FIRM_DELETED:
                evictFirmCaches(firmId);
                break;

            /* =======================
               REVIEW CHANGES
            ======================== */
            case REVIEW_CREATED:
            case REVIEW_UPDATED:
            case REVIEW_DELETED:
                evictReviewCaches(firmId, reviewId);
                break;

            /* =======================
               CHALLENGE CHANGES
            ======================== */
            case CHALLENGE_CREATED:
            case CHALLENGE_UPDATED:
            case CHALLENGE_DELETED:
                evictChallengeCaches(firmId, challengeId);
                break;
        }
    }

    /* --------------------
       Helper methods
    -------------------- */

    private void evictFirmCaches(Long firmId) {
        evict("FIRMS_BY_ID", firmId);
        clear("FIRMS_LIST");
        clear("FIRMS_LITE");
    }

    private void evictReviewCaches(Long firmId, Long reviewId) {
        evict("REVIEWS_BY_ID", reviewId);
        evict("REVIEWS_BY_FIRM", firmId);
        clear("REVIEWS_ALL");
    }

    private void evictChallengeCaches(Long firmId, Long challengeId) {
        evict("CHALLENGES_BY_ID", challengeId);
        evict("CHALLENGES_BY_FIRM", firmId);
        clear("CHALLENGES_ALL");
    }

    private void clear(String cacheName) {
        Cache c = cacheManager.getCache(cacheName);
        if (c != null) c.clear();
    }

    private void evict(String cacheName, Object key) {
        if (key == null) return;
        Cache c = cacheManager.getCache(cacheName);
        if (c != null) c.evict(key);
    }

}
