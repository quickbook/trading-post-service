package com.tps.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tps.cache.CacheNames;
import com.tps.cache.events.DataChangedEvent;
import com.tps.dto.request.ChallengeRequest;
import com.tps.dto.response.ChallengeResponse;
import com.tps.enums.EventChangeType;
import com.tps.exceptions.ResourceNotFoundException;
import com.tps.mapper.ChallengeMapper;
import com.tps.model.FirmChallenge;
import com.tps.repository.FirmChallengeRepository;
import com.tps.repository.FirmRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FirmChallengeService {

    private final FirmChallengeRepository challengeRepository;
    private final FirmRepository firmRepository;
    private final ChallengeMapper challengeMapper;
    private final ApplicationEventPublisher eventPublisher;

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

        // publish event for cache invalidation AFTER commit
        try {
            eventPublisher.publishEvent(new DataChangedEvent(EventChangeType.CHALLENGE_CREATED, request.getFirmId(),null,savedEntity.getId()));
            log.debug("Published FirmChangedEvent for firmId={} after creating challenge id={}", request.getFirmId(), savedEntity.getId());
        } catch (Exception ex) {
            log.warn("Failed to publish FirmChangedEvent for firmId={}", request.getFirmId(), ex);
        }

        return challengeMapper.toResponseDto(savedEntity);
    }

    // --- GET by Challenge ID ---
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.CHALLENGES_BY_ID, key = "#challengeId", unless = "#result == null")
    public ChallengeResponse getById(Long challengeId) {
        FirmChallenge entity = findChallengeById(challengeId);
        return challengeMapper.toResponseDto(entity);
    }

    // --- GET All Challenges by Firm ID ---
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.CHALLENGES_BY_FIRM, key = "#firmId", unless = "#result == null || #result.size() == 0")
    public List<ChallengeResponse> getByFirmId(Long firmId) {
        if (!firmRepository.existsById(firmId)) {
            throw new ResourceNotFoundException("Firm not found with id: " + firmId);
        }

        List<FirmChallenge> entities = challengeRepository.findByFirmCardIdOrderByProfitTargetPctDescAccountSizeUsdDescPriceAmountDesc(firmId);
        return entities.stream()
            .map(challengeMapper::toResponseDto)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.CHALLENGES_ALL,unless = "#result == null || #result.size() == 0")
    public List<ChallengeResponse> getAllChallenges() {
    
        List<FirmChallenge> entities = challengeRepository.findAll();

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

        // publish event so caches for this firm get invalidated AFTER_COMMIT
        try {
        	 eventPublisher.publishEvent(new DataChangedEvent(EventChangeType.CHALLENGE_UPDATED, request.getFirmId(),null,savedEntity.getId()));
            log.debug("Published FirmChangedEvent for firmId={} after updating challenge id={}", request.getFirmId(), savedEntity.getId());
        } catch (Exception ex) {
            log.warn("Failed to publish FirmChangedEvent for firmId={}", request.getFirmId(), ex);
        }

        return challengeMapper.toResponseDto(savedEntity);
    }

    @Transactional
    public void delete(Long challengeId) {

        FirmChallenge existing = challengeRepository.findById(challengeId)
                .orElseThrow(() -> new ResourceNotFoundException("Challenge not found with id: " + challengeId));

        Long firmId = existing.getFirmCard() != null ? existing.getFirmCard().getId() : null;

        challengeRepository.deleteById(challengeId);

        // publish event for cache invalidation AFTER_COMMIT
        if (firmId != null) {
            try {
            	 eventPublisher.publishEvent(new DataChangedEvent(EventChangeType.CHALLENGE_DELETED, null,null,challengeId));
                log.debug("Published FirmChangedEvent for firmId={} after deleting challenge id={}", firmId, challengeId);
            } catch (Exception ex) {
                log.warn("Failed to publish FirmChangedEvent for firmId={}", firmId, ex);
            }
        }
    }
}
