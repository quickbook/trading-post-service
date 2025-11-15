package com.tps.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.tps.dto.FirmLiteDto;
import com.tps.dto.FirmPatchRequest;
import com.tps.dto.FirmQuery;
import com.tps.dto.FirmResponse;
import com.tps.dto.FirmReviewDto;
import com.tps.exceptions.DuplicateResourceException;
import com.tps.exceptions.ResourceNotFoundException;
import com.tps.mapper.FirmMapper;
import com.tps.model.ChallengeCardView; // NEW: View Entity
import com.tps.model.FirmCard;
import com.tps.model.FirmStatus;
import com.tps.repository.ChallengeCardViewRepository; // NEW: View Repository
import com.tps.repository.FirmRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FirmService {

	private final FirmRepository firmRepository;
	private final FirmMapper firmMapper;
    private final ChallengeCardViewRepository challengeCardViewRepository;
    private final FirmReviewService firmReviewService;
	
	 @Transactional(readOnly = true)
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
	 
	 public List<FirmLiteDto> getAllFirmIdsAndNames() {
	        return firmRepository.findAll().stream()
	                .map(firmMapper::toLiteDto)
	                .collect(Collectors.toList());
	    }
	
     // 2. getById() - Fetches challenge card data and uses detailed mapper
	 @Transactional(readOnly = true)
	 public FirmResponse getById(Long id) {
	     log.info("Fetching firm by ID: {}", id);
	     FirmCard entity = firmRepository.findById(id)
	             .orElseThrow(() -> {
	                 log.warn("Firm with ID {} not found.", id);
	                 return new ResourceNotFoundException("Firm not found with id: " + id);
	             });
                 
        // Fetch related challenge cards from the view
        List<ChallengeCardView> challengeCards = challengeCardViewRepository.findByFirmId(id);
        List<FirmReviewDto> reviews = firmReviewService.getReviewsForFirm(id);
        // Use the detailed mapper method to construct the DTO with cards
	    return firmMapper.toDto(entity, challengeCards, reviews);
	 }
	
	
	 @Transactional
	 public FirmResponse createFirm(FirmPatchRequest firm) {
	     log.info("Attempting to create a new firm with name: {}", firm.getName());
	     
	     
	     if (firmRepository.existsByName(firm.getName())) {
	    	 throw new DuplicateResourceException("A firm with the name '" + firm.getName() + "' already exists.");
	     }
	     FirmCard entityToSave = firmMapper.toEntity(firm);
	     
         entityToSave.setFirmStatus(FirmStatus.ACTIVE);
         
	   
	     
	     
	     firmMapper.linkChildEntities(entityToSave);
	     
	     FirmCard savedEntity = firmRepository.save(entityToSave);
	     log.info("Successfully created firm with ID: {}", savedEntity.getId());
	     
	     return getById(savedEntity.getId());
	 }
	
	
	 @Transactional
	 public FirmResponse updateFirm(Long id, FirmPatchRequest firm) {
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
	     
	      
	     //existingEntity.setUpdatedBy(firm.getUserId());
	     firmMapper.updateSimpleFields(existingEntity, firm);
	
	     firmMapper.updatePlatformCollection(existingEntity, firm);
	
	     firmRepository.save(existingEntity);
	     log.info("Successfully updated firm with ID: {}", id);
	     
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
	 }
	
	 @Transactional(readOnly = true)	
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
	 public FirmResponse patchFirm(Long id, FirmPatchRequest partialFirmDto) {
	     log.info("Attempting to patch firm with ID: {}", id);
	     FirmCard existingEntity = firmRepository.findById(id)
	             .orElseThrow(() -> {
	                 log.warn("Firm with ID {} not found for patch.", id);
	                 return new ResourceNotFoundException("Firm not found with id: " + id);
	             });

	     if (StringUtils.hasText(partialFirmDto.getName())) {
	         if (!partialFirmDto.getName().equals(existingEntity.getName())) {
	             firmRepository.findByNameAndIdNot(partialFirmDto.getName(), id)
	             .ifPresent(conflict -> {
	                 throw new DuplicateResourceException("A firm with the name '" + partialFirmDto.getName() + "' already exists.");
	             });
	         }
	         existingEntity.setName(partialFirmDto.getName());
	     }
	     if (StringUtils.hasText(partialFirmDto.getSlug())) {
	         existingEntity.setSlug(partialFirmDto.getSlug());
	     }
	     if (partialFirmDto.getRating() != null) {
	         existingEntity.setRating(partialFirmDto.getRating());
	     }
	     if (partialFirmDto.getAllRatings() != null) {
	         existingEntity.setAllRatings(partialFirmDto.getAllRatings());
	     }
	    /* if (partialFirmDto.getMaxAccountSizeUsd() != null) {
	         existingEntity.setMaxAccountSizeUsd(partialFirmDto.getMaxAccountSizeUsd());
	     }
	     if (partialFirmDto.getProfitSplitPct() != null) {
	         existingEntity.setProfitSplit(partialFirmDto.getProfitSplitPct());
	     }
	     if (partialFirmDto.getFirmStatus() != null) {
	         existingEntity.setFirmStatus(partialFirmDto.getFirmStatus());
	     }
	     


	     if (partialFirmDto.getUpdated() != null) {
	         existingEntity.setUpdated(partialFirmDto.getUpdated());
	     }
	     
	     if(partialFirmDto.getUserId() == null) {
	         throw new IllegalArgumentException("User ID must be provided to patch a firm");
	     }*/
	   //  existingEntity.setUpdatedBy(partialFirmDto.getUserId());

	     log.warn("PATCH operation only updated simple top-level fields for Firm ID: {}. Collections/Nested objects were ignored.", id);

	     firmRepository.save(existingEntity);
	     log.info("Successfully patched simple fields for firm with ID: {}", id);


	     // FIX: Recurse to getById(id) to ensure response contains challenge cards
	     return getById(id);
	 }
}