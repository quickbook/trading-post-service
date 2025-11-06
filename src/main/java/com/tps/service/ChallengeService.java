package com.tps.service;

import com.tps.dto.Challenge;
import com.tps.exceptions.ResourceNotFoundException;
import com.tps.mapper.FirmMapper;
import com.tps.model.FirmCard;
import com.tps.repository.ChallengeRepository;
import com.tps.repository.FirmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final FirmRepository firmRepository;
    private final FirmMapper firmMapper; // We re-use FirmMapper's toDto/toEntity methods

    /**
     * Creates a new Challenge and associates it with a Firm.
     */
    public Challenge createChallenge(Long firmId, Challenge challengeDto) {
        log.info("Creating new challenge for firm ID: {}", firmId);
        
        // 1. Find the parent firm
        FirmCard firm = firmRepository.findById(firmId)
                .orElseThrow(() -> new ResourceNotFoundException("Firm not found with id: " + firmId));

        // 2. Map DTO to Entity
        com.tps.model.Challenge challengeEntity = firmMapper.toEntity(challengeDto);

        // 3. Set relationships
        challengeEntity.setFirmCard(firm);
        firm.getChallenges().add(challengeEntity);
        firmMapper.linkChallengeChildEntities(challengeEntity); // Links phases to challenge

        // 4. Save and return DTO
        com.tps.model.Challenge savedEntity = challengeRepository.save(challengeEntity);
        log.info("Successfully created challenge with ID: {}", savedEntity.getId());
        
        return firmMapper.toDto(savedEntity);
    }

    /**
     * Get a single challenge by its ID.
     */
    @Transactional(readOnly = true)
    public Challenge getChallengeById(Long challengeId) {
        com.tps.model.Challenge entity = challengeRepository.findByIdWithPhases(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));
        return firmMapper.toDto(entity);
    }

    /**
     * Get all challenges for a specific Firm.
     */
    @Transactional(readOnly = true)
    public List<Challenge> getChallengesForFirm(Long firmId) {
        if (!firmRepository.existsById(firmId)) {
            throw new ResourceNotFoundException("Firm not found with id: " + firmId);
        }
        
        List<com.tps.model.Challenge> entities = challengeRepository.findByFirmCardId(firmId);
        return entities.stream()
                .map(firmMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Updates an existing challenge.
     */
    public Challenge updateChallenge(Long challengeId, Challenge challengeDto) {
        log.info("Updating challenge ID: {}", challengeId);
        
        com.tps.model.Challenge existingEntity = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));

        // Use mapper to convert DTO to a new (transient) entity
        com.tps.model.Challenge updatedData = firmMapper.toEntity(challengeDto);

        // Manually update fields
        existingEntity.setName(updatedData.getName());
        existingEntity.setMaxDailyLossPct(updatedData.getMaxDailyLossPct());
        existingEntity.setMaxOverallLossPct(updatedData.getMaxOverallLossPct());

        // Update phases collection
        if (existingEntity.getPhases() != null) {
            existingEntity.getPhases().clear();
        } else {
            existingEntity.setPhases(new java.util.HashSet<>());
        }
        
        if (updatedData.getPhases() != null) {
            updatedData.getPhases().forEach(phase -> {
                phase.setChallenge(existingEntity); // Link phase to existing challenge
                existingEntity.getPhases().add(phase);
            });
        }

        com.tps.model.Challenge savedEntity = challengeRepository.save(existingEntity);
        return firmMapper.toDto(savedEntity);
    }

    /**
     * Deletes a challenge by its ID.
     */
    public void deleteChallenge(Long challengeId) {
        log.info("Deleting challenge ID: {}", challengeId);
        if (!challengeRepository.existsById(challengeId)) {
            throw new ResourceNotFoundException("Challenge not found with id: " + challengeId);
        }
        challengeRepository.deleteById(challengeId);
        log.info("Successfully deleted challenge ID: {}", challengeId);
    }
}