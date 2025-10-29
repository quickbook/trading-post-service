package com.tps.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tps.dto.Firm;
import com.tps.repository.FirmRepository;
import com.tps.model.FirmCard;
import com.tps.mapper.FirmMapper; 



import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor // Automatically creates constructor for final fields
public class FirmService {

	private final FirmRepository repo;
	private final FirmMapper firmMapper;
	

 
 @Transactional(readOnly = true)
 public List<Firm> getAll() {
     try {
         log.info("Fetching all firms from the database.");
         
         List<FirmCard> firmEntities = repo.findAll();

         List<Firm> firmDtos = firmEntities.stream()
                                           .map(firmMapper::toDto) //Maps entities to DTO's
                                           .collect(Collectors.toList());
         
         log.info("Successfully fetched and mapped {} firms.", firmDtos.size());
         return firmDtos;
     } catch (Exception ex) {
         log.error("Failed to fetch firms from database.", ex);
         return Collections.emptyList();
     }
 }
 
 
 @Transactional
 public Firm createFirm(Firm firmDto) {
     log.info("Attempting to create a new firm with title: {}", firmDto.getTitle());
     FirmCard entityToSave = firmMapper.toEntity(firmDto);
     
     /**
      * Set the necessary bidirectional links between parent and child entities.
      * This step is correctly manages relationships between Parent and child tables and save
      * foreign keys when persisting the entities (e.g., setting FirmCard on Platform,
      * Challenge on Phase).
      */
     firmMapper.linkChildEntities(entityToSave); //map
     
     FirmCard savedEntity = repo.save(entityToSave);
     log.info("Successfully created firm with ID: {}", savedEntity.getId());
     
     return firmMapper.toDto(savedEntity);
 }

 
 @Transactional
 public Firm updateFirm(Long id, Firm firmDto) {
     log.info("Attempting to update firm with ID: {}", id);

     FirmCard existingEntity = repo.findById(id)
             .orElseThrow(() -> {
                 log.warn("Firm with ID {} not found for update.", id);
                 return new RuntimeException("Firm not found with id: " + id);
             });

     firmMapper.updateSimpleFields(existingEntity, firmDto);

     firmMapper.updatePlatformCollection(existingEntity, firmDto);
     firmMapper.updateChallengeRelationship(existingEntity, firmDto);

     FirmCard savedEntity = repo.save(existingEntity);
     log.info("Successfully updated firm with ID: {}", id);
     
     return firmMapper.toDto(savedEntity);
 }


 @Transactional
 public void deleteFirm(Long id) {
     log.info("Attempting to delete firm with ID: {}", id);
     // 1. Check existence
     if (!repo.existsById(id)) {
         log.warn("Firm with ID {} not found for deletion.", id);
         throw new RuntimeException("Firm not found with id: " + id);
     }
     // 2. Delete
     repo.deleteById(id);
     log.info("Successfully deleted firm with ID: {}", id);
 }

 
}