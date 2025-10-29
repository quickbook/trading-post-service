package com.tps.mapper;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

// Import DTOs
import com.tps.dto.Firm;
import com.tps.dto.Challenge;
import com.tps.dto.Phase;
import com.tps.dto.Platform;

// Import Entities
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
        entity.setAssets(dto.getAssets());
        entity.setMaxAllocation(dto.getMaxAllocation());

        if (dto.getPlatforms() != null) {
            entity.setPlatforms(dto.getPlatforms().stream()
                                   .map(this::toEntity) // Calls toEntity(Platform DTO)
                                   .collect(Collectors.toList()));
        }
        if (dto.getChallenge() != null) {
            entity.setChallenge(toEntity(dto.getChallenge())); // Calls toEntity(Challenge DTO)
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
                               .map(this::toEntity) // Calls toEntity(Phase DTO)
                               .collect(Collectors.toList()));
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
         // challenge link is set in the service
        return entity;
    }


    /**
     * Translates a FirmCard Entity (from Database) to a Firm DTO (for API Response).
     * @param entity The FirmCard entity.
     * @return The corresponding Firm DTO.
     */
    public Firm toDto(FirmCard entity) {
        if (entity == null) return null;

        Firm dto = new Firm();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setProfitSplit(entity.getProfitSplit());
        dto.setAccount(entity.getAccount());
        dto.setCode(entity.getCode());
        dto.setLogo(entity.getLogo());
        dto.setUpdated(entity.isUpdated());
        dto.setRating(entity.getRating());
        dto.setAllRatings(entity.getAllRatings());
        dto.setCountry(entity.getCountry());
        dto.setFlag(entity.getFlag());
        dto.setAssets(entity.getAssets());
        dto.setMaxAllocation(entity.getMaxAllocation());

        if (entity.getPlatforms() != null) {
            dto.setPlatforms(entity.getPlatforms().stream()
                                   .map(this::toDto) // Calls toDto(Platform Entity)
                                   .collect(Collectors.toList()));
        }
        if (entity.getChallenge() != null) {
            dto.setChallenge(toDto(entity.getChallenge())); // Calls toDto(Challenge Entity)
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

    /** Translates Challenge Entity to Challenge DTO. */
    public Challenge toDto(com.tps.model.Challenge entity) {
        if (entity == null) return null;
        Challenge dto = new Challenge();
        dto.setName(entity.getName());
        dto.setMaxDailyLossPct(entity.getMaxDailyLossPct());
        dto.setMaxOverallLossPct(entity.getMaxOverallLossPct());
        if (entity.getPhases() != null) {
            dto.setPhases(entity.getPhases().stream()
                                .map(this::toDto) // Calls toDto(Phase Entity)
                                .collect(Collectors.toList()));
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

    // --- Update Logic Helpers (Used only by FirmService.updateFirm) ---

    /**
     * [UPDATE HELPER] Updates the simple fields of an existing FirmCard entity from a Firm DTO.
     * @param existingEntity The entity fetched from the database.
     * @param firmDto The DTO containing the new data.
     */
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
        existingEntity.setAssets(firmDto.getAssets()); // Replace the list of strings
        existingEntity.setMaxAllocation(firmDto.getMaxAllocation());
    }

    /**
     * [UPDATE HELPER] Updates the platforms collection for an existing FirmCard.
     * Uses a clear-and-re-add strategy.
     * @param existingEntity The entity fetched from the database.
     * @param firmDto The DTO containing the new platform data.
     */
    public void updatePlatformCollection(FirmCard existingEntity, Firm firmDto) {
        // Clear existing platforms
        if (existingEntity.getPlatforms() != null) {
            existingEntity.getPlatforms().clear();
        } else {
            existingEntity.setPlatforms(new java.util.ArrayList<>());
        }

        // Add new platforms from the DTO
        if (firmDto.getPlatforms() != null) {
            List<com.tps.model.Platform> newPlatforms = firmDto.getPlatforms().stream()
                    .map(this::toEntity) // Convert DTOs to Entities
                    .peek(p -> p.setFirmCard(existingEntity)) // Set back-link
                    .collect(Collectors.toList());
            existingEntity.getPlatforms().addAll(newPlatforms);
        }
    }

    /**
     * [UPDATE HELPER] Updates the challenge relationship for an existing FirmCard.
     * @param existingEntity The entity fetched from the database.
     * @param firmDto The DTO containing the potential new challenge data.
     */
    public void updateChallengeRelationship(FirmCard existingEntity, Firm firmDto) {
        if (firmDto.getChallenge() != null) {
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

     /**
      * [CREATE/UPDATE HELPER] Sets the required bidirectional links for JPA relationships before saving.
      * @param entity The `FirmCard` entity being prepared for saving.
      */
     public void linkChildEntities(FirmCard entity) {
         if (entity.getPlatforms() != null) {
             entity.getPlatforms().forEach(p -> p.setFirmCard(entity));
         }
         if (entity.getChallenge() != null && entity.getChallenge().getPhases() != null) {
             entity.getChallenge().getPhases().forEach(ph -> ph.setChallenge(entity.getChallenge()));
         }
     }
}