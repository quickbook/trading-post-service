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

import com.tps.dto.Firm;
import com.tps.dto.FirmPatchRequest;
import com.tps.dto.FirmQuery;
import com.tps.dto.FirmResponse;
import com.tps.exceptions.DuplicateResourceException;
import com.tps.exceptions.ResourceNotFoundException;
import com.tps.mapper.FirmMapper;
import com.tps.model.FirmCard;
import com.tps.repository.FirmRepository;

import jakarta.persistence.criteria.Predicate;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor 
public class FirmService {

	private final FirmRepository firmRepository;
	private final FirmMapper firmMapper;
	
	 @Transactional(readOnly = true)
	 public List<FirmResponse> getAll() {
	     try {
	         log.info("Fetching all firms from the database.");
	         
	         List<FirmCard> firmEntities = firmRepository.findAll();
	
	         List<FirmResponse> firmDtos = firmEntities.stream()
	                                           .map(firmMapper::toDto) //Maps entities to DTO's
	                                           .collect(Collectors.toList());
	         
	         log.info("Successfully fetched and mapped {} firms.", firmDtos.size());
	         return firmDtos;
	     } catch (Exception ex) {
	         log.error("Failed to fetch firms from database.", ex);
	         return Collections.emptyList();
	     }
	 }
	 
	 @Transactional(readOnly = true)
	 public FirmResponse getById(Long id) {
	     log.info("Fetching firm by ID: {}", id);
	     FirmCard entity = firmRepository.findById(id)
	             .orElseThrow(() -> {
	                 log.warn("Firm with ID {} not found.", id);
	                 return new ResourceNotFoundException("Firm not found with id: " + id);
	             });
	     return firmMapper.toDto(entity);
	 }
	 
	 
	 @Transactional
	 public FirmResponse createFirm(Firm firm) {
	     log.info("Attempting to create a new firm with title: {}", firm.getTitle());
	     
	     if(firm.getUserId() == null) {
             // You can also get this from the SecurityContext if you implement AuditorAware
             throw new IllegalArgumentException("User ID must be provided to create a firm");
         }
	     if (firmRepository.existsByTitle(firm.getTitle())) {
	    	 throw new DuplicateResourceException("A firm with the title '" + firm.getTitle() + "' already exists.");
	     }
	     FirmCard entityToSave = firmMapper.toEntity(firm);
	     
	     if(firm.getUserId() == null) {
             // You can also get this from the SecurityContext if you implement AuditorAware
             throw new IllegalArgumentException("User ID must be provided to create a firm");
         }
	     entityToSave.setCreatedBy(firm.getUserId());
	     
	     /**
	      * Set the necessary bidirectional links between parent and child entities.
	      * This step is correctly manages relationships between Parent and child tables and save
	      * foreign keys when persisting the entities (e.g., setting FirmCard on Platform,
	      * Challenge on Phase).
	      */
	     firmMapper.linkChildEntities(entityToSave); //map
	     
	     FirmCard savedEntity = firmRepository.save(entityToSave);
	     log.info("Successfully created firm with ID: {}", savedEntity.getId());
	     
	     return firmMapper.toDto(savedEntity);
	 }
	 
	 
	 
	
	 
	 @Transactional
	 public FirmResponse updateFirm(Long id, Firm firm) {
	     log.info("Attempting to update firm with ID: {}", id);
	
	     FirmCard existingEntity = firmRepository.findById(id)
	             .orElseThrow(() -> {
	                 log.warn("Firm with ID {} not found for update.", id);
	                 return new ResourceNotFoundException("Firm not found with id: " + id);
	             });
	     
	     firmRepository.findByTitleAndIdNot(firm.getTitle(), id)
         .ifPresent(conflict -> {
             throw new DuplicateResourceException("A firm with the title '" + firm.getTitle() + "' already exists.");
         });
	     
	     if(firm.getUserId() == null) {
             throw new IllegalArgumentException("User ID must be provided to update a firm");
         }
	     existingEntity.setUpdatedBy(firm.getUserId());
	     firmMapper.updateSimpleFields(existingEntity, firm);
	
	     firmMapper.updatePlatformCollection(existingEntity, firm);
	
	     FirmCard savedEntity = firmRepository.save(existingEntity);
	     log.info("Successfully updated firm with ID: {}", id);
	     
	     return firmMapper.toDto(savedEntity);
	 }
	
	
	 @Transactional
	 public void deleteFirm(Long id) {
	     log.info("Attempting to delete firm with ID: {}", id);
	     // 1. Check existence
	     if (!firmRepository.existsById(id)) {
	         log.warn("Firm with ID {} not found for deletion.", id);
	         throw new ResourceNotFoundException("Firm not found with id: " + id);
	     }
	     // 2. Delete
	     firmRepository.deleteById(id);
	     log.info("Successfully deleted firm with ID: {}", id);
	 }
	
	 @Transactional(readOnly = true) 
	 public Page<FirmResponse> find(@Valid FirmQuery query, Pageable pageable) {
	     
	     Specification<FirmCard> spec = (root, criteriaQuery, cb) -> {
	         
	         List<Predicate> predicates = new ArrayList<>();
	
	         
	         if (query.getMinAccount() != null) {
	             predicates.add(cb.greaterThanOrEqualTo(root.get("account"), query.getMinAccount()));
	         }
	
	         if (StringUtils.hasText(query.getCountry())) {
	             predicates.add(cb.equal(cb.lower(root.get("country")), query.getCountry().toLowerCase()));
	         }
	
	         if (query.getUpdated() != null) {
	             predicates.add(cb.equal(root.get("updated"), query.getUpdated()));
	         }
	
	
	         return cb.and(predicates.toArray(new Predicate[0]));
	     };
	
	     Page<FirmCard> firmCardPage = firmRepository.findAll(spec, pageable);
	     
	     return firmCardPage.map(firmMapper::toDto);
	 }

//--- UPDATE (PATCH) ---
	 @Transactional
	 public FirmResponse patchFirm(Long id, FirmPatchRequest partialFirmDto) {
	     log.info("Attempting to patch firm with ID: {}", id);
	     FirmCard existingEntity = firmRepository.findById(id) // Fetch existing entity
	             .orElseThrow(() -> {
	                  log.warn("Firm with ID {} not found for patch.", id);
	                 return new ResourceNotFoundException("Firm not found with id: " + id);
	             });

	     if (StringUtils.hasText(partialFirmDto.getTitle())) {
	         existingEntity.setTitle(partialFirmDto.getTitle());
	     }
	     if (partialFirmDto.getProfitSplit() != null) { 
	          existingEntity.setProfitSplit(partialFirmDto.getProfitSplit());
	     }
	     if (partialFirmDto.getAccount() != null) {
	         existingEntity.setAccount(partialFirmDto.getAccount());
	     }
	     if (StringUtils.hasText(partialFirmDto.getCode())) {
	         existingEntity.setCode(partialFirmDto.getCode());
	     }
	     if (StringUtils.hasText(partialFirmDto.getLogo())) {
	          existingEntity.setLogo(partialFirmDto.getLogo());
	     }
	     if (StringUtils.hasText(partialFirmDto.getRating())) {
	          existingEntity.setRating(partialFirmDto.getRating());
	     }
	      if (partialFirmDto.getAllRatings() != null) { 
	          existingEntity.setAllRatings(partialFirmDto.getAllRatings());
	     }
	      if (StringUtils.hasText(partialFirmDto.getCountry())) {
	          existingEntity.setCountry(partialFirmDto.getCountry());
	     }
	      if (StringUtils.hasText(partialFirmDto.getFlag())) {
	          existingEntity.setFlag(partialFirmDto.getFlag());
	     }
	      if (partialFirmDto.getMaxAllocation() != null) {
	          existingEntity.setMaxAllocation(partialFirmDto.getMaxAllocation());
	     }
         // This is the new field we added to the DTO
         if (partialFirmDto.getUpdated() != null) {
             existingEntity.setUpdated(partialFirmDto.getUpdated());
         }
         
         if(partialFirmDto.getUserId() == null) {
             throw new IllegalArgumentException("User ID must be provided to patch a firm");
         }
         existingEntity.setUpdatedBy(partialFirmDto.getUserId());

	     log.warn("PATCH operation only updated simple top-level fields for Firm ID: {}. Collections/Nested objects were ignored.", id);

	     FirmCard savedEntity = firmRepository.save(existingEntity);
	     log.info("Successfully patched simple fields for firm with ID: {}", id);


        return firmMapper.toDto(savedEntity);
	 }

 
}