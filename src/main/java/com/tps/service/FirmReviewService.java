package com.tps.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tps.dto.CreateReviewRequest;
import com.tps.dto.FirmReviewDto;
import com.tps.exceptions.ResourceNotFoundException;
import com.tps.mapper.FirmReviewMapper;
import com.tps.model.FirmCard;
import com.tps.model.FirmReview;
import com.tps.model.User;
import com.tps.repository.FirmRepository;
import com.tps.repository.FirmReviewRepository;
import com.tps.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class FirmReviewService {

    private final FirmReviewRepository reviewRepository;
    private final FirmRepository firmRepository;
    private final UserRepository userRepository;
    private final FirmReviewMapper reviewMapper;

    /**
     * Create a new review for a firm.
     */
    public FirmReviewDto createReview(Long firmId, Long userId,CreateReviewRequest dto) {
        FirmCard firm = firmRepository.findById(firmId)
                .orElseThrow(() -> new ResourceNotFoundException("Firm not found with id: " + firmId));

       User user =  userRepository.findById(userId)
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
        return reviewMapper.toDto(savedReview);
    }
    
    @Transactional(readOnly = true)
    public List<FirmReviewDto> getReviewsForFirm(Long firmId) {
        // First, check if the firm exists
        if (!firmRepository.existsById(firmId)) {
            throw new ResourceNotFoundException("Firm not found with id: " + firmId);
        }
        
        return reviewRepository.findByFirmIdAndIsDeletedFalse(firmId)
                .stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<FirmReviewDto> getAllActiveReviews() {
        return reviewRepository.findByIsDeletedFalse()
                .stream()
                .map(reviewMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public FirmReviewDto getReviewById(Long reviewId) {
        FirmReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        if (review.getIsDeleted()) {
             throw new ResourceNotFoundException("Review not found with id: " + reviewId);
        }

        return reviewMapper.toDto(review);
    }
    
    public void deleteReview(Long reviewId) {
        FirmReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: "+ reviewId));
        
        review.setIsDeleted(true);
        reviewRepository.save(review);
    }
    
}