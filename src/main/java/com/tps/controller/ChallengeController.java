package com.tps.controller;

import com.tps.dto.ApiResponse;
import com.tps.dto.Challenge;
import com.tps.service.ChallengeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/tradingpost/api/v1") 
@RequiredArgsConstructor
@Validated
public class ChallengeController {

    private final ChallengeService challengeService;

    
    @PostMapping("/firms/{firmId}/challenges")
    public ResponseEntity<ApiResponse<Challenge>> createChallenge(
            @PathVariable Long firmId,
            @Valid @RequestBody Challenge challengeDto,
            HttpServletRequest request) {
        
        Challenge created = challengeService.createChallenge(firmId, challengeDto);
        
        // Build URI for the new challenge, e.g., /api/v1/challenges/{newId}
        URI location = URI.create(String.format("/tradingpost/api/v1/challenges/%d", created.getId())); 
        // Note: Challenge DTO has no ID, so we can't build a perfect URL. 
        // A better DTO would include the ID. For now, this is a placeholder.

        return ResponseEntity.created(location)
                .body(ApiResponse.<Challenge>builder()
                        .success(true)
                        .message("Challenge created successfully")
                        .data(created)
                        .status(HttpStatus.CREATED)
                        .path(request.getRequestURI())
                        .timestamp(System.currentTimeMillis())
                        .build());
    }

    
    @GetMapping("/firms/{firmId}/challenges")
    public ResponseEntity<ApiResponse<List<Challenge>>> getChallengesForFirm(
            @PathVariable Long firmId,
            HttpServletRequest request) {
        
        List<Challenge> challenges = challengeService.getChallengesForFirm(firmId);
        
        return ResponseEntity.ok(
                ApiResponse.<List<Challenge>>builder()
                        .success(true)
                        .message("Challenges for firm fetched successfully")
                        .data(challenges)
                        .status(HttpStatus.OK)
                        .path(request.getRequestURI())
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    
    @GetMapping("/challenges/{challengeId}")
    public ResponseEntity<ApiResponse<Challenge>> getChallengeById(
            @PathVariable Long challengeId,
            HttpServletRequest request) {
        
        Challenge challenge = challengeService.getChallengeById(challengeId);
        
        return ResponseEntity.ok(
                ApiResponse.<Challenge>builder()
                        .success(true)
                        .message("Challenge fetched successfully")
                        .data(challenge)
                        .status(HttpStatus.OK)
                        .path(request.getRequestURI())
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    
    @PutMapping("/challenges/{challengeId}")
    public ResponseEntity<ApiResponse<Challenge>> updateChallenge(
            @PathVariable Long challengeId,
            @Valid @RequestBody Challenge challengeDto,
            HttpServletRequest request) {
        
        Challenge updated = challengeService.updateChallenge(challengeId, challengeDto);
        
        return ResponseEntity.ok(
                ApiResponse.<Challenge>builder()
                        .success(true)
                        .message("Challenge updated successfully")
                        .data(updated)
                        .status(HttpStatus.OK)
                        .path(request.getRequestURI())
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    @DeleteMapping("/challenges/{challengeId}")
    public ResponseEntity<ApiResponse<Object>> deleteChallenge(
            @PathVariable Long challengeId,
            HttpServletRequest request) {
        
        challengeService.deleteChallenge(challengeId);
        
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Challenge deleted successfully")
                        .data(null)
                        .status(HttpStatus.OK)
                        .path(request.getRequestURI())
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }
}