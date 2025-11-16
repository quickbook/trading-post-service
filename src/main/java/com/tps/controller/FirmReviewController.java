package com.tps.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tps.dto.request.ReviewRequest;
import com.tps.dto.response.ApiResponse;
import com.tps.dto.response.ReviewResponse;
import com.tps.service.FirmReviewService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tradingpost/api/v1/reviews") 
@RequiredArgsConstructor
@Validated
public class FirmReviewController {

    private final FirmReviewService reviewService;

    /**
     * Create a new review for a specific firm.
     */
    @PostMapping("/firm/{firmId}/{userId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long firmId,@PathVariable Long userId,
            @Valid @RequestBody ReviewRequest dto,
            HttpServletRequest request) {
        
        ReviewResponse createdReview = reviewService.createReview(firmId, userId,dto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.<ReviewResponse>builder()
                .success(true)
                .message("Review created successfully")
                .data(createdReview)
                .status(HttpStatus.CREATED)
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }
    
    @GetMapping("/firm/{firmId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviewsForFirm(
            @PathVariable Long firmId, HttpServletRequest request) {
        
        List<ReviewResponse> reviews = reviewService.getReviewsForFirm(firmId);
        
        return ResponseEntity.ok(
            ApiResponse.<List<ReviewResponse>>builder()
                .success(true)
                .message("Reviews fetched successfully")
                .data(reviews)
                .status(HttpStatus.OK)
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getAllReviews(HttpServletRequest request) {
        
        List<ReviewResponse> reviews = reviewService.getAllActiveReviews();
        
        return ResponseEntity.ok(
            ApiResponse.<List<ReviewResponse>>builder()
                .success(true)
                .message("All active reviews fetched successfully")
                .data(reviews)
                .status(HttpStatus.OK)
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }
    
    @GetMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewById(
            @PathVariable Long reviewId, HttpServletRequest request) {
        
        ReviewResponse review = reviewService.getReviewById(reviewId);
        
        return ResponseEntity.ok(
            ApiResponse.<ReviewResponse>builder()
                .success(true)
                .message("Review fetched successfully")
                .data(review)
                .status(HttpStatus.OK)
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse<Object>> deleteReview(
            @PathVariable Long reviewId,
            HttpServletRequest request) {
        
        reviewService.deleteReview(reviewId);
        
        return ResponseEntity.ok(
            ApiResponse.builder()
                .success(true)
                .message("Review deleted successfully")
                .data(null)
                .status(HttpStatus.OK)
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }
}