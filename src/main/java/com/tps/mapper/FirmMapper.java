package com.tps.mapper;

import java.util.ArrayList;
import java.util.HashSet; // Import HashSet
import java.util.Set; // Import Set
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

// Import DTOs
import com.tps.dto.Firm;
import com.tps.dto.Challenge;
import com.tps.dto.Phase;
import com.tps.dto.Platform;

import com.tps.model.FirmCard;


@Component
public class FirmMapper {


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
                                   .map(this::toEntity)
                                   .collect(Collectors.toSet())); 
        } else {
             entity.setPlatforms(new HashSet<>()); 
        }
        if (dto.getChallenge() != null) {
            entity.setChallenge(toEntity(dto.getChallenge()));
        } else {
            entity.setChallenge(null);
        }
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
         entity.setName(dto.getName());
         entity.setMaxDailyLossPct(dto.getMaxDailyLossPct());
         entity.setMaxOverallLossPct(dto.getMaxOverallLossPct());
         if (dto.getPhases() != null) {
             entity.setPhases(dto.getPhases().stream()
                                .map(this::toEntity)
                                .collect(Collectors.toSet())); 
         } else {
             entity.setPhases(new HashSet<>()); 
         }
         return entity;
    }

    /** Translates Phase DTO to Phase Entity. */
    public com.tps.model.Phase toEntity(Phase dto) {
        if (dto == null) return null;
        com.tps.model.Phase entity = new com.tps.model.Phase();
        entity.setPhase(dto.getPhase());
        entity.setProfitTargetPct(dto.getProfitTargetPct());
        entity.setMinTradingDays(dto.getMinTradingDays());
        entity.setTimeLimitDays(dto.getTimeLimitDays());
        return entity;
    }

    // --- Entity -> DTO Translation ---

    
    public Firm toDto(FirmCard entity) {
        if (entity == null) return null;

        Firm dto = new Firm();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());

        Integer profitSplitValue = entity.getProfitSplit();
        dto.setProfitSplit(profitSplitValue != null ? profitSplitValue : 0);

        dto.setAccount(entity.getAccount());
        dto.setCode(entity.getCode());
        dto.setLogo(entity.getLogo());
        dto.setUpdated(entity.isUpdated());
        dto.setRating(entity.getRating());

        Integer allRatingsValue = entity.getAllRatings();
        dto.setAllRatings(allRatingsValue != null ? allRatingsValue : 0);

        dto.setCountry(entity.getCountry());
        dto.setFlag(entity.getFlag());
        // Convert Set<String> from Entity to List<String> for DTO
        if (entity.getAssets() != null) {
            dto.setAssets(new ArrayList<>(entity.getAssets())); // Convert Set to List
        } else {
             dto.setAssets(new ArrayList<>());
        }
        dto.setMaxAllocation(entity.getMaxAllocation());

        // Convert Set<PlatformEntity> to List<PlatformDto>
        if (entity.getPlatforms() != null) {
            dto.setPlatforms(entity.getPlatforms().stream()
                                   .map(this::toDto)
                                   .collect(Collectors.toList())); // Collect to List for DTO
        } else {
            dto.setPlatforms(new ArrayList<>());
        }
        if (entity.getChallenge() != null) {
            dto.setChallenge(toDto(entity.getChallenge()));
        } else {
            dto.setChallenge(null);
        }
        return dto;
    }

     /** Translates Platform Entity to Platform DTO. */
    public Platform toDto(com.tps.model.Platform entity) {
        if (entity == null) return null;
        Platform dto = new Platform();
        dto.setSrc(entity.getSrc());
        dto.setAlt(entity.getAlt());
        return dto;
    }

    /** Translates Challenge Entity (using Set) to Challenge DTO (using List). */
    public Challenge toDto(com.tps.model.Challenge entity) {
        if (entity == null) return null;
        Challenge dto = new Challenge();
        dto.setName(entity.getName());
        dto.setMaxDailyLossPct(entity.getMaxDailyLossPct());
        dto.setMaxOverallLossPct(entity.getMaxOverallLossPct());
         // Convert Set<PhaseEntity> to List<PhaseDto>
        if (entity.getPhases() != null) {
             dto.setPhases(entity.getPhases().stream()
                                 .map(this::toDto)
                                 .collect(Collectors.toList())); // Collect to List for DTO
        } else {
             dto.setPhases(new ArrayList<>());
        }
        return dto;
    }

    /** Translates Phase Entity to Phase DTO. */
    public Phase toDto(com.tps.model.Phase entity) {
        if (entity == null) return null;
        Phase dto = new Phase();
        dto.setPhase(entity.getPhase());
        dto.setProfitTargetPct(entity.getProfitTargetPct());
        dto.setMinTradingDays(entity.getMinTradingDays());
        dto.setTimeLimitDays(entity.getTimeLimitDays());
        return dto;
    }

    // --- Update Logic Helpers (Adjusted for Set) ---

     /** [UPDATE HELPER] Updates simple fields */
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
         // Convert List<String> DTO to Set<String> Entity
         if (firmDto.getAssets() != null) {
             existingEntity.setAssets(new HashSet<>(firmDto.getAssets())); // Use HashSet
         } else {
              existingEntity.setAssets(new HashSet<>()); // Use HashSet
         }
         existingEntity.setMaxAllocation(firmDto.getMaxAllocation());
     }

     /** [UPDATE HELPER] Updates platforms collection (now uses Set) */
     public void updatePlatformCollection(FirmCard existingEntity, Firm firmDto) {
         if (existingEntity.getPlatforms() != null) {
             existingEntity.getPlatforms().clear();
         } else {
             existingEntity.setPlatforms(new HashSet<>()); // Use HashSet
         }
         if (firmDto.getPlatforms() != null) {
             // Convert List<Dto> to Set<Entity>
             Set<com.tps.model.Platform> newPlatforms = firmDto.getPlatforms().stream()
                     .map(this::toEntity)
                     .peek(p -> p.setFirmCard(existingEntity))
                     .collect(Collectors.toSet()); // Collect to Set
             existingEntity.getPlatforms().addAll(newPlatforms);
         }
     }

     /** [UPDATE HELPER] Updates challenge relationship (now uses Set for phases) */
     public void updateChallengeRelationship(FirmCard existingEntity, Firm firmDto) {
         if (firmDto.getChallenge() != null) {
             // toEntity now creates ChallengeEntity with Set<PhaseEntity>
             com.tps.model.Challenge updatedChallengeEntity = toEntity(firmDto.getChallenge());
             if (existingEntity.getChallenge() != null) {
                 updatedChallengeEntity.setId(existingEntity.getChallenge().getId());
             }
             if (updatedChallengeEntity.getPhases() != null) {
                 updatedChallengeEntity.getPhases().forEach(ph -> ph.setChallenge(updatedChallengeEntity));
             }
             existingEntity.setChallenge(updatedChallengeEntity);
         } else {
             existingEntity.setChallenge(null);
         }
     }

     /** [CREATE/UPDATE HELPER] Sets bidirectional links */
     public void linkChildEntities(FirmCard entity) {
         if (entity.getPlatforms() != null) {
             entity.getPlatforms().forEach(p -> p.setFirmCard(entity));
         }
         if (entity.getChallenge() != null && entity.getChallenge().getPhases() != null) {
             entity.getChallenge().getPhases().forEach(ph -> ph.setChallenge(entity.getChallenge()));
         }
     }
     
     
}

