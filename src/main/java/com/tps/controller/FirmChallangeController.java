package com.tps.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tps.dto.request.ChallengeRequest;
import com.tps.dto.response.ApiResponse;
import com.tps.dto.response.ReviewResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tradingpost/api/v1/challanges") 
@RequiredArgsConstructor
@Validated
public class FirmChallangeController {

    @PostMapping("/firm/{firmId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable Long firmId,@PathVariable Long userId,
            @Valid @RequestBody ChallengeRequest dtoRequest,
            HttpServletRequest request) {
        
      //  ReviewResponse createdReview = reviewService.createReview(firmId, userId,dto);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.<ReviewResponse>builder()
                .success(true)
                .message("Review created successfully")
                //.data(createdReview)
                .status(HttpStatus.CREATED)
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }
}
