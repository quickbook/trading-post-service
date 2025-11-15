package com.tps.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tps.dto.AboutDto;
import com.tps.dto.ChallengeCardDto;
import com.tps.dto.FirmLiteDto;
// Import all DTOs
import com.tps.dto.FirmPatchRequest;
import com.tps.dto.FirmResponse;
import com.tps.dto.FirmReviewDto; // NEW IMPORT
import com.tps.dto.PriceDto;
import com.tps.dto.TradingConditionsDto;
import com.tps.exceptions.ResourceNotFoundException;
import com.tps.model.ChallengeCardView;
import com.tps.model.Country;
// Import all Models and Repositories
import com.tps.model.FirmCard;
import com.tps.model.FirmStatus;
import com.tps.model.TradingPlatform;
import com.tps.model.WithdrawalSpeed;
import com.tps.repository.CountryRepository;
import com.tps.repository.TradingPlatformRepository;

import lombok.RequiredArgsConstructor; 


@Component
@RequiredArgsConstructor 
public class FirmMapper {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TradingPlatformRepository tradingPlatformRepository; 
    private final CountryRepository countryRepository;

    // --- MAPPING VIEW ENTITY TO DTO ---
    
    public FirmLiteDto toLiteDto(FirmCard entity) {
        if (entity == null) return null;
        return new FirmLiteDto(entity.getId(), entity.getName());
    }
    
    public ChallengeCardDto toDto(ChallengeCardView view) {
        if (view == null) return null;
        
        PriceDto price = new PriceDto(view.getPriceAmount(), view.getPriceCurrency());
        
        return new ChallengeCardDto(
            view.getPlanId(),
            view.getTierName(),
            view.getPhaseLabel(),
            view.getProfitTargetPct(),
            view.getDailyLossPct(),
            view.getMaxLossPct(),
            view.getAccountSizeUsd(),
            price,
            view.getBuyUrl()
        );
    }
    

    // --- Entity -> DTO (Detailed GET: Accepts view data and reviews) ---

    public FirmResponse toDto(FirmCard entity, List<ChallengeCardView> challengeCards, List<FirmReviewDto> reviews) {
        if (entity == null) return null;

        FirmResponse dto = new FirmResponse(); 
        
        // 1. Map Top-Level Fields
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        dto.setWebsite(entity.getWebsite());
        dto.setLogo(entity.getLogo());
        dto.setCountryCode(entity.getCountryCode()); 
        String countryName = countryRepository.findByCode(entity.getCountryCode())
                .map(Country::getName)
                .orElse(entity.getCountryCode());
        dto.setCountry(countryName);
        dto.setIsTrusted(entity.getIsTrusted());
        dto.setRating(entity.getRating());
        dto.setAllRatings(entity.getAllRatings());
        dto.setDescription(entity.getDescription());
        //dto.setFirmPageURL(entity.getBuyUrl());
        dto.setBuyUrl(entity.getBuyUrl());
        dto.setFirmType(entity.getFirmType());        
      


        // 2. Map TradingConditions (Reconstruct nested DTO)
        TradingConditionsDto conditionsDto = new TradingConditionsDto();
        conditionsDto.setMaximumAccountSizeUsd(entity.getMaxAccountSizeUsd());
        conditionsDto.setProfitSplitPct(entity.getProfitSplit() != null ? entity.getProfitSplit().intValue() : null);
        conditionsDto.setDiscountCode(entity.getDiscountCode());
        conditionsDto.setWithdrawalSpeed(mapWithdrawalSpeedToString(entity.getWithdrawalSpeed()));

        try {
            if (entity.getKeyFeatures() != null) {
                conditionsDto.setKeyFeatures(objectMapper.readValue(entity.getKeyFeatures(), objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)));
            } else { conditionsDto.setKeyFeatures(new ArrayList<>()); }
        } catch (JsonProcessingException e) {
             throw new RuntimeException("Error converting keyFeatures JSON string to List", e);
        }
        
        if (entity.getPlatforms() != null) {
            conditionsDto.setTradingPlatforms(new ArrayList<>(entity.getPlatforms()).stream()
                                .map(this::mapEntityToPlatformCode) 
                                .collect(Collectors.toList()));
        } else { conditionsDto.setTradingPlatforms(new ArrayList<>()); }
        
        if (entity.getAssets() != null) {
            conditionsDto.setAvailableAssets(new ArrayList<>(entity.getAssets()));
        } else { conditionsDto.setAvailableAssets(new ArrayList<>()); }
        dto.setTradingConditions(conditionsDto);


        // 3. Map About (Reconstruct nested DTO)
        AboutDto aboutDto = new AboutDto();
        aboutDto.setLegalName(entity.getLegalName());
        aboutDto.setRegistrationNo(entity.getRegistrationNo());
        aboutDto.setEstablishedDate(entity.getEstablishedDate());
        aboutDto.setFounders(entity.getFounders());
        aboutDto.setHeadquarters(entity.getHeadquarters());
        aboutDto.setJurisdiction(entity.getJurisdiction());
        aboutDto.setFirmStatus(mapFirmStatusToString(entity.getFirmStatus()));
        aboutDto.setFoundedYear(entity.getFoundedYear());
        aboutDto.setDescription(entity.getAboutDescription());
        dto.setAbout(aboutDto);
        
        // 4. Map Challenges
        dto.setChallenges(challengeCards.stream()
            .map(this::toDto) // Use the ChallengeCardView to DTO mapper
            .collect(Collectors.toList()));

        // 5. Map Reviews (NEW)
        dto.setReviews(reviews);
        
        return dto;
    }
    
    // --- Entity -> DTO (Simple GET: Used by list/find endpoint) ---
    /**
     * Default signature used by list endpoints (find/getAll) where challenge card data and reviews are not needed.
     */
    public FirmResponse toDto(FirmCard entity) {
        return toDto(entity, new ArrayList<>(), new ArrayList<>()); // Calls the detailed method with empty lists
    }
    
    // --- DTO -> Entity (For POST/PUT operations) ---

    public FirmCard toEntity(FirmPatchRequest dto) {
        if (dto == null) return null;

        FirmCard entity = new FirmCard();
        
        // 1. Map Top-Level Fields
        entity.setName(dto.getName());
        entity.setSlug(dto.getSlug());
        entity.setWebsite(dto.getWebsite());
        entity.setLogo(dto.getLogo());
        String countryCode = dto.getCountryCode();
        if (countryCode != null) {
            countryRepository.findByCode(countryCode)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found for code: " + countryCode));
        }
        entity.setCountryCode(countryCode);      
        entity.setIsTrusted(dto.getIsTrusted());
        entity.setRating(dto.getRating());
        entity.setAllRatings(dto.getAllRatings());
        entity.setDescription(dto.getDescription());
        entity.setUpdated(true); 
        entity.setFirmType(dto.getFirmType()); 
        entity.setBuyUrl(dto.getFirmPageURL()); 


        // 2. Map TradingConditions (flattened)
        TradingConditionsDto conditions = dto.getTradingConditions();
        if (conditions != null) {
            entity.setMaxAccountSizeUsd(conditions.getMaximumAccountSizeUsd());
            entity.setProfitSplit(conditions.getProfitSplitPct() != null ? conditions.getProfitSplitPct().shortValue() : null);
            entity.setDiscountCode(conditions.getDiscountCode());
            entity.setWithdrawalSpeed(mapWithdrawalSpeed(conditions.getWithdrawalSpeed()));
            
            try {
                if (conditions.getKeyFeatures() != null) {
                    entity.setKeyFeatures(objectMapper.writeValueAsString(conditions.getKeyFeatures()));
                }
            } catch (JsonProcessingException e) {
                 throw new RuntimeException("Error converting keyFeatures to JSON string", e);
            }
            
            if (conditions.getTradingPlatforms() != null) {
                entity.setPlatforms(conditions.getTradingPlatforms().stream()
                                   .map(this::mapPlatformCodeToEntity)
                                   .collect(Collectors.toSet()));
            } else { entity.setPlatforms(new HashSet<>()); }
            
            if (conditions.getAvailableAssets() != null) {
                 entity.setAssets(new HashSet<>(conditions.getAvailableAssets())); 
            } else { entity.setAssets(new HashSet<>()); }
        } else {
             entity.setPlatforms(new HashSet<>()); 
             entity.setAssets(new HashSet<>());
        }
        
        // 3. Map About (flattened)
        AboutDto about = dto.getAbout();
        if (about != null) {
            entity.setLegalName(about.getLegalName());
            entity.setRegistrationNo(about.getRegistrationNo());
            entity.setEstablishedDate(about.getEstablishedDate());
            entity.setFounders(about.getFounders());
            entity.setHeadquarters(about.getHeadquarters());
            entity.setJurisdiction(about.getJurisdiction());
            entity.setFirmStatus(mapFirmStatus(about.getFirmStatus()));
            
            entity.setFoundedYear(about.getFoundedYear());
            entity.setAboutDescription(about.getDescription());

        }

        return entity;
    }
    
    // Looks up the TradingPlatform entity by code and sets it on the Platform join entity.
    private com.tps.model.Platform mapPlatformCodeToEntity(String code) {
        // 1. Look up the domain entity by the code (e.g., "MT5")
        TradingPlatform domainPlatform = tradingPlatformRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Trading platform not found for code: " + code));
        
        // 2. Create the Platform join entity
        com.tps.model.Platform entity = new com.tps.model.Platform();
        entity.setDomainPlatform(domainPlatform); // 3. Set the mandatory FK entity
        
        return entity;
    }
    
    // Helper to map Platform entity back to a code string
    private String mapEntityToPlatformCode(com.tps.model.Platform entity) {
        return entity.getDomainPlatform() != null ? entity.getDomainPlatform().getCode() : "UNKNOWN";
    }

    // --- Enum Helpers ---

    private WithdrawalSpeed mapWithdrawalSpeed(String value) {
        if (value == null) return null;
        try {
            return WithdrawalSpeed.valueOf(value.toUpperCase().replace(' ', '_').replace('-', '_'));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String mapWithdrawalSpeedToString(WithdrawalSpeed speed) {
        return speed != null ? speed.getValue() : null;
    }
    
    private FirmStatus mapFirmStatus(String value) {
        if (value == null) return FirmStatus.ACTIVE;
        try {
            return FirmStatus.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return FirmStatus.ACTIVE;
        }
    }
    
    private String mapFirmStatusToString(FirmStatus status) {
        return status != null ? status.name() : FirmStatus.ACTIVE.name();
    }
    

    // --- Update Logic Helpers ---

     public void updateSimpleFields(FirmCard existingEntity, FirmPatchRequest firmDto) {
         // 1. Map Top-Level Fields
        existingEntity.setName(firmDto.getName());
        existingEntity.setSlug(firmDto.getSlug());
        existingEntity.setWebsite(firmDto.getWebsite());
        existingEntity.setLogo(firmDto.getLogo());
        existingEntity.setCountryCode(firmDto.getCountryCode());

        existingEntity.setIsTrusted(firmDto.getIsTrusted());
        existingEntity.setRating(firmDto.getRating());
        existingEntity.setAllRatings(firmDto.getAllRatings());
        existingEntity.setDescription(firmDto.getDescription());
        existingEntity.setUpdated(true);

        // 2. Map TradingConditions (flattened)
        TradingConditionsDto conditions = firmDto.getTradingConditions();
        if (conditions != null) {
            existingEntity.setMaxAccountSizeUsd(conditions.getMaximumAccountSizeUsd());
            existingEntity.setProfitSplit(conditions.getProfitSplitPct() != null ? conditions.getProfitSplitPct().shortValue() : null);
            existingEntity.setDiscountCode(conditions.getDiscountCode());
            existingEntity.setWithdrawalSpeed(mapWithdrawalSpeed(conditions.getWithdrawalSpeed()));
            
            try {
                if (conditions.getKeyFeatures() != null) {
                    existingEntity.setKeyFeatures(objectMapper.writeValueAsString(conditions.getKeyFeatures()));
                } else { existingEntity.setKeyFeatures(null); }
            } catch (JsonProcessingException e) {
                 throw new RuntimeException("Error converting keyFeatures to JSON string", e);
            }
            
            if (conditions.getAvailableAssets() != null) {
                 existingEntity.setAssets(new HashSet<>(conditions.getAvailableAssets())); 
            } else {
                 existingEntity.setAssets(new HashSet<>()); 
            }
        }
        
        // 3. Map About (flattened)
        AboutDto about = firmDto.getAbout();
        if (about != null) {
            existingEntity.setLegalName(about.getLegalName());
            existingEntity.setRegistrationNo(about.getRegistrationNo());
            existingEntity.setEstablishedDate(about.getEstablishedDate());
            existingEntity.setFounders(about.getFounders());
            existingEntity.setHeadquarters(about.getHeadquarters());
            existingEntity.setJurisdiction(about.getJurisdiction());
            existingEntity.setFirmStatus(mapFirmStatus(about.getFirmStatus()));
            existingEntity.setFoundedYear(about.getFoundedYear());
        }
     }
     
     // Method to update the Platform Collection (OneToMany relationship)
     public void updatePlatformCollection(FirmCard existingEntity, FirmPatchRequest firmDto) {
         
         if (existingEntity.getPlatforms() != null) {
             existingEntity.getPlatforms().clear();
         } else {
             existingEntity.setPlatforms(new HashSet<>());
         }
         
         // Use nested TradingConditions DTO
         List<String> platformCodes = firmDto.getTradingConditions().getTradingPlatforms();
         
         if (platformCodes != null) {
             Set<com.tps.model.Platform> newPlatforms = platformCodes.stream()
                     .map(this::mapPlatformCodeToEntity)
                     .peek(p -> p.setFirmCard(existingEntity))
                     .collect(Collectors.toSet());
             existingEntity.getPlatforms().addAll(newPlatforms);
         }
     }
     
     // Links Platforms back to the FirmCard after mapping
     public void linkChildEntities(FirmCard entity) {
         if (entity.getPlatforms() != null) {
             entity.getPlatforms().forEach(p -> p.setFirmCard(entity));
         }
     }
}