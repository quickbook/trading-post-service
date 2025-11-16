package com.tps.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tps.dto.request.ChallengeRequest;
import com.tps.dto.response.ChallengeResponse;
import com.tps.exceptions.ResourceNotFoundException;
import com.tps.mapper.ChallengeMapper;
import com.tps.model.FirmChallenge; // Uses mutable entity
import com.tps.repository.FirmChallengeRepository;
import com.tps.repository.FirmRepository; 

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeService {

    private final FirmChallengeRepository challengeRepository; 
    private final FirmRepository firmRepository; 
    private final ChallengeMapper challengeMapper; 

    // Helper method to ensure the Challenge exists
    private FirmChallenge findChallengeById(Long challengeId) {
        return challengeRepository.findById(challengeId)
            .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));
    }
    
    @Transactional
    public ChallengeResponse create(ChallengeRequest request) {
        
        if (!firmRepository.existsById(request.getFirmId())) {
             throw new ResourceNotFoundException("Firm not found with id: " + request.getFirmId());
        }
        FirmChallenge entity = challengeMapper.toEntity(request);
        FirmChallenge savedEntity = challengeRepository.save(entity);
        return challengeMapper.toResponseDto(savedEntity);
    }

    // --- GET by Challenge ID ---
    @Transactional(readOnly = true)
    public ChallengeResponse getById(Long challengeId) {
        FirmChallenge entity = findChallengeById(challengeId);
        return challengeMapper.toResponseDto(entity);
    }
    
    // --- GET All Challenges by Firm ID ---
    @Transactional(readOnly = true)
    public List<ChallengeResponse> getByFirmId(Long firmId) {
        if (!firmRepository.existsById(firmId)) {
             throw new ResourceNotFoundException("Firm not found with id: " + firmId);
        }
        
        List<FirmChallenge> entities = challengeRepository.findByFirmCardId(firmId);

        return entities.stream()
            .map(challengeMapper::toResponseDto)
            .collect(Collectors.toList());
    }

    // --- UPDATE ---
    @Transactional
    public ChallengeResponse update(Long challengeId, ChallengeRequest request) {
        
        FirmChallenge existingEntity = findChallengeById(challengeId);
        
        if (!existingEntity.getFirmCard().getId().equals(request.getFirmId())) {
             throw new IllegalArgumentException("Cannot change the firm association for an existing challenge.");
        }
        
        FirmChallenge updatedEntity = challengeMapper.toEntity(request); 
        updatedEntity.setId(challengeId); // Preserve the existing ID
        updatedEntity.setFirmCard(existingEntity.getFirmCard()); // Preserve the existing Firm FK reference

        FirmChallenge savedEntity = challengeRepository.save(updatedEntity);
        
        return challengeMapper.toResponseDto(savedEntity);
    }

    
    @Transactional
    public void delete(Long challengeId) {
        
        if (!challengeRepository.existsById(challengeId)) {
            throw new ResourceNotFoundException("Challenge not found with id: " + challengeId);
        }

        challengeRepository.deleteById(challengeId);
    }
}