package com.tps.mapper;

import java.util.ArrayList;
import java.util.HashSet; 
import java.util.Set; 
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

// Import all DTOs
import com.tps.dto.Challenge;
import com.tps.dto.Firm;
import com.tps.dto.FirmResponse;
import com.tps.dto.Phase;
import com.tps.dto.Platform;

// Import all Models
import com.tps.model.FirmCard;


@Component
public class FirmMapper {

    // --- DTO -> Entity ---

    public FirmCard toEntity(Firm dto) {
        if (dto == null) return null;

        FirmCard entity = new FirmCard();
        entity.setTitle(dto.getTitle());
        entity.setProfitSplit(dto.getProfitSplit()); 
        entity.setAccount(dto.getAccount());
        entity.setCode(dto.getCode());
        entity.setLogo(dto.getLogo());
        entity.setUpdated(dto.isUpdated());
        entity.setRating(dto.getRating());
        entity.setAllRatings(dto.getAllRatings()); 
        entity.setCountry(dto.getCountry());
        entity.setFlag(dto.getFlag());

        if (dto.getAssets() != null) {
             entity.setAssets(new HashSet<>(dto.getAssets())); 
        } else {
            entity.setAssets(new HashSet<>()); 
        }
        entity.setMaxAllocation(dto.getMaxAllocation());

        if (dto.getPlatforms() != null) {
            entity.setPlatforms(dto.getPlatforms().stream()
                                   .map(this::toEntity) // maps dto.Platform to model.Platform
                                   .collect(Collectors.toSet())); 
        } else {
             entity.setPlatforms(new HashSet<>()); 
        }
        // challenge logic was correctly removed from here
        return entity;
    }

    
    public com.tps.model.Platform toEntity(Platform dto) {
        if (dto == null) return null;
        com.tps.model.Platform entity = new com.tps.model.Platform();
        entity.setSrc(dto.getSrc());
        entity.setAlt(dto.getAlt());
        return entity;
    }

    public com.tps.model.Challenge toEntity(Challenge dto) {
         if (dto == null) return null;
         com.tps.model.Challenge entity = new com.tps.model.Challenge();
         // Note: ID is not mapped from DTO to entity, this is correct for creation
         entity.setName(dto.getName());
         entity.setMaxDailyLossPct(dto.getMaxDailyLossPct());
         entity.setMaxOverallLossPct(dto.getMaxOverallLossPct());
         if (dto.getPhases() != null) {
             entity.setPhases(dto.getPhases().stream()
                                .map(this::toEntity) // maps dto.Phase to model.Phase
                                .collect(Collectors.toSet())); 
         } else {
             entity.setPhases(new HashSet<>()); 
         }
         return entity;
    }

    public com.tps.model.Phase toEntity(Phase dto) {
        if (dto == null) return null;
        com.tps.model.Phase entity = new com.tps.model.Phase();
        entity.setPhase(dto.getPhase());
        entity.setProfitTargetPct(dto.getProfitTargetPct());
        entity.setMinTradingDays(dto.getMinTradingDays());
        entity.setTimeLimitDays(dto.getTimeLimitDays());
        return entity;
    }

    // --- Entity -> DTO ---

    public FirmResponse toDto(FirmCard entity) {
        if (entity == null) return null;

        FirmResponse dto = new FirmResponse(); // <-- Correct type
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());

        Integer profitSplitValue = entity.getProfitSplit();
        dto.setProfitSplit(profitSplitValue != null ? profitSplitValue : 0);

        dto.setAccount(entity.getAccount());
        dto.setCode(entity.getCode());
        dto.setLogo(entity.getLogo());
        dto.setUpdated(entity.isUpdated());
        dto.setRating(entity.getRating());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUpdatedDate(entity.getUpdatedDate());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setUpdatedBy(entity.getUpdatedBy());

        Integer allRatingsValue = entity.getAllRatings();
        dto.setAllRatings(allRatingsValue != null ? allRatingsValue : 0);

        dto.setCountry(entity.getCountry());
        dto.setFlag(entity.getFlag());
        
        // --- THIS IS THE FIX ---
        // Copy the lazy Set to a new ArrayList *before* streaming.
        // This fully loads the collection and prevents ConcurrentModificationException.
        
        if (entity.getAssets() != null) {
            dto.setAssets(new ArrayList<>(entity.getAssets()));
        } else {
             dto.setAssets(new ArrayList<>());
        }
        
        if (entity.getPlatforms() != null) {
            dto.setPlatforms(new ArrayList<>(entity.getPlatforms()).stream() // <-- Fixed
                                   .map(this::toDto)
                                   .collect(Collectors.toList()));
        } else {
            dto.setPlatforms(new ArrayList<>());
        }

        if (entity.getChallenges() != null) {
             dto.setChallenges(new ArrayList<>(entity.getChallenges()).stream() // <-- Fixed
                                 .map(this::toDto)
                                 .collect(Collectors.toList()));
        } else {
             dto.setChallenges(new ArrayList<>());
        }
        
        return dto;
    }

    public Platform toDto(com.tps.model.Platform entity) {
        if (entity == null) return null;
        Platform dto = new Platform();
        dto.setSrc(entity.getSrc());
        dto.setAlt(entity.getAlt());
        return dto;
    }

    public Challenge toDto(com.tps.model.Challenge entity) {
        if (entity == null) return null;
        Challenge dto = new Challenge();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setMaxDailyLossPct(entity.getMaxDailyLossPct());
        dto.setMaxOverallLossPct(entity.getMaxOverallLossPct());
         
        if (entity.getPhases() != null) {
             dto.setPhases(new ArrayList<>(entity.getPhases()).stream() // <-- Fixed
                                 .map(this::toDto)
                                 .collect(Collectors.toList()));
        } else {
             dto.setPhases(new ArrayList<>());
        }
        return dto;
    }

    public Phase toDto(com.tps.model.Phase entity) {
        if (entity == null) return null;
        Phase dto = new Phase();
        dto.setPhase(entity.getPhase());
        dto.setProfitTargetPct(entity.getProfitTargetPct());
        dto.setMinTradingDays(entity.getMinTradingDays());
        dto.setTimeLimitDays(entity.getTimeLimitDays());
        return dto;
    }

    // --- Update Logic Helpers ---

     public void updateSimpleFields(FirmCard existingEntity, Firm firmDto) {
         existingEntity.setTitle(firmDto.getTitle());
         existingEntity.setProfitSplit(firmDto.getProfitSplit());
         existingEntity.setAccount(firmDto.getAccount());
         existingEntity.setCode(firmDto.getCode());
         existingEntity.setLogo(firmDto.getLogo());
         existingEntity.setUpdated(firmDto.isUpdated());
         existingEntity.setRating(firmDto.getRating());
         existingEntity.setAllRatings(firmDto.getAllRatings());
         existingEntity.setCountry(firmDto.getCountry());
         existingEntity.setFlag(firmDto.getFlag());
         if (firmDto.getAssets() != null) {
             existingEntity.setAssets(new HashSet<>(firmDto.getAssets()));
         } else {
              existingEntity.setAssets(new HashSet<>());
         }
         existingEntity.setMaxAllocation(firmDto.getMaxAllocation());
     }

     public void updatePlatformCollection(FirmCard existingEntity, Firm firmDto) {
         if (existingEntity.getPlatforms() != null) {
             existingEntity.getPlatforms().clear();
         } else {
             existingEntity.setPlatforms(new HashSet<>());
         }
         if (firmDto.getPlatforms() != null) {
             Set<com.tps.model.Platform> newPlatforms = firmDto.getPlatforms().stream()
                     .map(this::toEntity)
                     .peek(p -> p.setFirmCard(existingEntity))
                     .collect(Collectors.toSet());
             existingEntity.getPlatforms().addAll(newPlatforms);
         }
     }

     public void linkChildEntities(FirmCard entity) {
         if (entity.getPlatforms() != null) {
             entity.getPlatforms().forEach(p -> p.setFirmCard(entity));
         }
     }
     
     public void linkChallengeChildEntities(com.tps.model.Challenge entity) {
         if (entity.getPhases() != null) {
             entity.getPhases().forEach(ph -> ph.setChallenge(entity));
         }
     }
}