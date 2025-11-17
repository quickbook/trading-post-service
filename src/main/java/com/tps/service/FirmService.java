package com.tps.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.context.ApplicationEventPublisher;

import com.tps.cache.CacheNames;
import com.tps.cache.events.FirmChangedEvent;
import com.tps.dto.FirmLiteDto;
import com.tps.dto.FirmQuery;
import com.tps.dto.request.FirmRequest;
import com.tps.dto.response.FirmResponse;
import com.tps.dto.response.ReviewResponse;
import com.tps.enums.FirmStatus;
import com.tps.exceptions.DuplicateResourceException;
import com.tps.exceptions.ResourceNotFoundException;
import com.tps.mapper.FirmMapper;
import com.tps.model.ViewFirmChallenges; // NEW: View Entity
import com.tps.model.FirmCard;
import com.tps.repository.ChallengeCardViewRepository; // NEW: View Repository
import com.tps.repository.FirmRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FirmService {

    private final FirmRepository firmRepository;
    private final FirmMapper firmMapper;
    private final ChallengeCardViewRepository challengeCardViewRepository;
    private final FirmReviewService firmReviewService;
    private final ApplicationEventPublisher eventPublisher;

    // --- READ methods (cached) ---

    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.FIRMS_LIST, unless = "#result == null || #result.size() == 0")
    public List<FirmResponse> getAll() {
        try {
            log.info("Fetching all active firms from the database.");
            List<FirmCard> firmEntities = firmRepository.findByFirmStatus(FirmStatus.ACTIVE);

            List<FirmResponse> firmDtos = firmEntities.stream()
                    .map(firmMapper::toDto) // Simple mapper call
                    .collect(Collectors.toList());

            log.info("Successfully fetched and mapped {} active firms.", firmDtos.size());
            return firmDtos;
        } catch (Exception ex) {
            log.error("Failed to fetch firms from database.", ex);
            return Collections.emptyList();
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.FIRMS_LITE, unless = "#result == null")
    public List<FirmLiteDto> getAllFirmIdsAndNames() {
        return firmRepository.findAll().stream()
                .map(firmMapper::toLiteDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.FIRMS_BY_ID, key = "#id", unless = "#result == null")
    public FirmResponse getById(Long id) {
        log.info("Fetching firm by ID: {}", id);
        FirmCard entity = firmRepository.findByIdWithDetails(id)
                .orElseThrow(() -> {
                    log.warn("Firm with ID {} not found.", id);
                    return new ResourceNotFoundException("Firm not found with id: " + id);
                });

        // Fetch related challenge cards from the view
        List<ViewFirmChallenges> challengeCards = challengeCardViewRepository.findByFirmId(id);
        List<ReviewResponse> reviews = firmReviewService.getReviewsForFirm(id);

        return firmMapper.toDto(entity, challengeCards, reviews);
    }

    // --- WRITE methods: publish event and persist ---

    @Transactional
    public FirmResponse createFirm(@Valid FirmRequest firm) {
        log.info("Attempting to create a new firm with name: {}", firm.getName());

        if (firmRepository.existsByName(firm.getName())) {
            throw new DuplicateResourceException("A firm with the name '" + firm.getName() + "' already exists.");
        }
        FirmCard entityToSave = firmMapper.toEntity(firm);
        entityToSave.setFirmStatus(FirmStatus.ACTIVE);

        // ensure children are linked so that cascade persists them
        firmMapper.linkChildEntities(entityToSave);

        FirmCard savedEntity = firmRepository.save(entityToSave);
        log.info("Successfully created firm with ID: {}", savedEntity.getId());

        // Publish event after saving — listener will evict caches AFTER commit
        eventPublisher.publishEvent(new FirmChangedEvent(savedEntity.getId()));

        return getById(savedEntity.getId());
    }

    @Transactional
    public FirmResponse updateFirm(Long id, @Valid FirmRequest firm) {
        log.info("Attempting to update firm with ID: {}", id);

        FirmCard existingEntity = firmRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Firm with ID {} not found for update.", id);
                    return new ResourceNotFoundException("Firm not found with id: " + id);
                });

        firmRepository.findByNameAndIdNot(firm.getName(), id)
                .ifPresent(conflict -> {
                    throw new DuplicateResourceException("A firm with the name '" + firm.getName() + "' already exists.");
                });

        // update
        firmMapper.updateSimpleFields(existingEntity, firm);
        firmMapper.updatePlatformCollection(existingEntity, firm);

        // ensure leverage child relationships are attached by mapper
        firmMapper.linkChildEntities(existingEntity);

        FirmCard saved = firmRepository.save(existingEntity);
        log.info("Successfully updated firm with ID: {}", id);

        // publish event to evict caches after commit
        eventPublisher.publishEvent(new FirmChangedEvent(saved.getId()));

        return getById(id);
    }

    @Transactional
    public void deleteFirm(Long id) {
        log.info("Attempting to delete firm with ID: {}", id);
        if (!firmRepository.existsById(id)) {
            log.warn("Firm with ID {} not found for deletion.", id);
            throw new ResourceNotFoundException("Firm not found with id: " + id);
        }
        firmRepository.deleteById(id);
        log.info("Successfully deleted firm with ID: {}", id);

        // publish event so caches are evicted after commit
        eventPublisher.publishEvent(new FirmChangedEvent(id));
    }

    // --- Search / find (paged): leave uncached or cache carefully with keys ---
    @Transactional(readOnly = true)
    @Cacheable(value = CacheNames.FIRMS_FIND_LIST, unless = "#result == null || #result.totalElements == 0")
    public Page<FirmResponse> find(@Valid FirmQuery query, Pageable pageable) {

        Specification<FirmCard> spec = (root, criteriaQuery, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("firmStatus"), FirmStatus.ACTIVE));

            if (query.getMinAccount() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("maxAccountSizeUsd"), query.getMinAccount()));
            }

            if (StringUtils.hasText(query.getCountry())) {
                predicates.add(cb.equal(cb.lower(root.get("hqCountry")), query.getCountry().toLowerCase()));
            }

            if (query.getUpdated() != null) {
                predicates.add(cb.equal(root.get("updated"), query.getUpdated()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<FirmCard> firmCardPage = firmRepository.findAll(spec, pageable);

        return firmCardPage.map(firmMapper::toDto);
    }

    @Transactional
    public FirmResponse patchFirm(Long id, FirmRequest partialFirmDto) {
        log.info("Attempting to patch firm with ID: {}", id);
        FirmCard existingEntity = firmRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Firm with ID {} not found for patch.", id);
                    return new ResourceNotFoundException("Firm not found with id: " + id);
                });

        // patch simple fields (keeps behavior as before)
        if (StringUtils.hasText(partialFirmDto.getName())) {
            if (!partialFirmDto.getName().equals(existingEntity.getName())) {
                firmRepository.findByNameAndIdNot(partialFirmDto.getName(), id)
                        .ifPresent(conflict -> {
                            throw new DuplicateResourceException("A firm with the name '" + partialFirmDto.getName() + "' already exists.");
                        });
            }
            existingEntity.setName(partialFirmDto.getName());
        }
        // ... other simple patches omitted for brevity (same as your existing implementation)

        firmRepository.save(existingEntity);
        log.info("Successfully patched simple fields for firm with ID: {}", id);

        // Evict caches after successful commit
        eventPublisher.publishEvent(new com.tps.cache.events.FirmChangedEvent(id));

        // return full resource
        return getById(id);
    }
}
