package com.tps.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tps.dto.request.ChallengeRequest;
import com.tps.dto.response.ApiResponse;
import com.tps.dto.response.ChallengeResponse;
import com.tps.service.FirmChallengeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tradingpost/api/v1/challenges") 
@RequiredArgsConstructor
@Validated
public class FirmChallangeController { 

    private final FirmChallengeService challengeService;

    
    @PostMapping
    public ResponseEntity<ApiResponse<ChallengeResponse>> create(
            @Valid @RequestBody ChallengeRequest request, 
            HttpServletRequest httpReq) { 
        
        ChallengeResponse created = challengeService.create(request); 

        return ResponseEntity.created(URI.create(httpReq.getRequestURI() + "/" + created.getId()))
                .body(ApiResponse.<ChallengeResponse>builder() 
                        .success(true)
                        .message("Challenge plan created successfully")
                        .data(created)
                        .status(HttpStatus.CREATED)
                        .path(httpReq.getRequestURI())
                        .timestamp(System.currentTimeMillis())
                        .build());
    }

    
    @GetMapping("/firm/{firmId}")
    public ResponseEntity<ApiResponse<List<ChallengeResponse>>> getByFirmId(
            @PathVariable Long firmId, 
            HttpServletRequest httpReq) {
        
        List<ChallengeResponse> challenges = challengeService.getByFirmId(firmId);

        return ResponseEntity.ok(ApiResponse.<List<ChallengeResponse>>builder() 
                .success(true)
                .message("Challenges fetched successfully for firm ID: " + firmId)
                .data(challenges)
                .status(HttpStatus.OK)
                .path(httpReq.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build());
    }
    
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChallengeResponse>> getById(
            @PathVariable Long id, 
            HttpServletRequest httpReq) {
        
        ChallengeResponse challenge = challengeService.getById(id);

        return ResponseEntity.ok(ApiResponse.<ChallengeResponse>builder() 
                .success(true)
                .message("Challenge fetched successfully")
                .data(challenge)
                .status(HttpStatus.OK)
                .path(httpReq.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build());
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<ChallengeResponse>>> getAllChallenges(
            HttpServletRequest httpReq) {
        
        List<ChallengeResponse> challenge = challengeService.getAllChallenges();

        return ResponseEntity.ok(ApiResponse.<List<ChallengeResponse>>builder() 
                .success(true)
                .message("All Challenges fetched successfully")
                .data(challenge)
                .status(HttpStatus.OK)
                .path(httpReq.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChallengeResponse>> update( 
            @PathVariable Long id,
            @Valid @RequestBody ChallengeRequest request,
            HttpServletRequest httpReq) {
        
        ChallengeResponse updated = challengeService.update(id, request); 

        return ResponseEntity.ok(ApiResponse.<ChallengeResponse>builder() 
                .success(true)
                .message("Challenge updated successfully")
                .data(updated)
                .path(httpReq.getRequestURI()) 
                .status(HttpStatus.OK)
                .timestamp(System.currentTimeMillis())
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(
            @PathVariable Long id, 
            HttpServletRequest httpReq) {
        
        challengeService.delete(id);

        ApiResponse<Object> response = ApiResponse.<Object>builder()
                .success(true)
                .message("Challenge deleted successfully")
                .data(null)
                .status(HttpStatus.OK) 
                .path(httpReq.getRequestURI()) 
                .timestamp(System.currentTimeMillis())
                .build();
        return ResponseEntity.ok(response); 
    }
}