package com.tps.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import com.tps.cache.CacheNames;
import com.tps.cache.events.DataChangedEvent;
import com.tps.dto.request.ReviewRequest;
import com.tps.dto.response.ReviewResponse;
import com.tps.enums.EventChangeType;
import com.tps.exceptions.ResourceNotFoundException;
import com.tps.mapper.FirmReviewMapper;
import com.tps.model.FirmCard;
import com.tps.model.FirmReview;
import com.tps.model.User;
import com.tps.repository.FirmRepository;
import com.tps.repository.FirmReviewRepository;
import com.tps.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class FirmReviewService {

    private final FirmReviewRepository reviewRepository;
    private final FirmRepository firmRepository;
    private final UserRepository userRepository;
    private final FirmReviewMapper reviewMapper;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Create a new review for a firm.
     * After save we publish FirmChangedEvent(firmId) so CacheInvalidationListener evicts review & firm caches AFTER_COMMIT.
     */
    public ReviewResponse createReview(Long firmId, Long userId, ReviewRequest dto) {
        log.debug("Creating review for firmId={}, userId={}", firmId, userId);

        FirmCard firm = firmRepository.findById(firmId)
                .orElseThrow(() -> new ResourceNotFoundException("Firm not found with id: " + firmId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        FirmReview review = new FirmReview();
        review.setFirm(firm);
        review.setTradingExp(dto.getTradingExp());
        review.setIsVrfdPurchase(false);
        review.setUser(user);
        review.setRating(dto.getRating());
        review.setDescription(dto.getDescription());
        review.setIsDeleted(false);

        FirmReview savedReview = reviewRepository.save(review);

        // publish event so listener can evict caches AFTER the transaction commits
        try {
        	 eventPublisher.publishEvent(new DataChangedEvent(EventChangeType.REVIEW_CREATED, firmId,savedReview.getId(),null));
            log.debug("Published FirmChangedEvent for firmId={}", firmId);
        } catch (Exception ex) {
            log.warn("Failed to publish FirmChangedEvent for firmId={}. This will not affect the saved review.", firmId, ex);
        }

        return reviewMapper.toDto(savedReview);
    }

    /**
     * Cached list of reviews for a firm. Keyed by firmId.
     * Don't cache empty lists.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.REVIEWS_BY_FIRM, key = "#firmId", unless = "#result == null || #result.size() == 0")
    public List<ReviewResponse> getReviewsForFirm(Long firmId) {
        // First, check if the firm exists
        if (!firmRepository.existsById(firmId)) {
            throw new ResourceNotFoundException("Firm not found with id: " + firmId);
        }

        return reviewRepository.findByFirmIdAndIsDeletedFalse(firmId)
                .stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Cached list of all active reviews. Single entry 'all'.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.REVIEWS_ALL, key = "'all'", unless = "#result == null || #result.size() == 0")
    public List<ReviewResponse> getAllActiveReviews() {
        return reviewRepository.findByIsDeletedFalse()
                .stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Cached single review by id.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.REVIEWS_BY_ID, key = "#reviewId", unless = "#result == null")
    public ReviewResponse getReviewById(Long reviewId) {
        FirmReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        if (review.getIsDeleted()) {
            throw new ResourceNotFoundException("Review not found with id: " + reviewId);
        }

        return reviewMapper.toDto(review);
    }

    /**
     * Soft-delete a review. After marking deleted and saving, publish FirmChangedEvent so caches are invalidated.
     */
    public void deleteReview(Long reviewId) {
        FirmReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        review.setIsDeleted(true);
        reviewRepository.save(review);

        Long firmId = review.getFirm() != null ? review.getFirm().getId() : null;
        if (firmId != null) {
            try {
            	 eventPublisher.publishEvent(new DataChangedEvent(EventChangeType.REVIEW_DELETED, firmId,reviewId,null));
                log.debug("Published FirmChangedEvent for firmId={} after deleting reviewId={}", firmId, reviewId);
            } catch (Exception ex) {
                log.warn("Failed to publish FirmChangedEvent for firmId={} after deleting reviewId={}", firmId, reviewId, ex);
            }
        }
    }
}
