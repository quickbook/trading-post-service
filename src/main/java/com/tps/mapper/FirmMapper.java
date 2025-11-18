package com.tps.mapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tps.dto.AboutDto; 
import com.tps.dto.FirmLiteDto;
import com.tps.dto.InstrumentLeverageDto;
import com.tps.dto.LeverageDto; 
import com.tps.dto.ProfitSplitOptionDto;
import com.tps.dto.TradingConditionsDto;
import com.tps.dto.request.FirmRequest;
import com.tps.dto.response.ChallengeResponse;
import com.tps.dto.response.FirmResponse;
import com.tps.dto.response.ReviewResponse;
import com.tps.enums.FirmStatus;
import com.tps.enums.LeverageProfile;
import com.tps.exceptions.ResourceNotFoundException;
import com.tps.model.DmnCountry;
import com.tps.model.DmnTradingPlatform;
import com.tps.model.FirmCard;
import com.tps.model.FirmLeverage;
import com.tps.model.FirmPlatform; 
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
 

    // --- Entity -> DTO (Detailed GET) ---

    public FirmResponse toDto(FirmCard entity, List<ChallengeResponse> challengeCards, List<ReviewResponse> reviews,boolean includeChallengesAndReviews) {
        if (entity == null) return null;

        FirmResponse dto = new FirmResponse();

        // 1. Top-Level
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setSlug(entity.getSlug());
        dto.setWebsite(entity.getWebsite());
        dto.setLogo(entity.getLogo());
        dto.setCountryCode(entity.getCountryCode());
        String countryName = countryRepository.findByCode(entity.getCountryCode())
                .map(DmnCountry::getName)
                .orElse(entity.getCountryCode());
        dto.setCountry(countryName);
        dto.setIsTrusted(entity.getIsTrusted());
        dto.setRating(entity.getRating());
        dto.setAllRatings(entity.getAllRatings());
        dto.setBuyUrl(entity.getBuyUrl());
        dto.setFirmType(entity.getFirmType());       

        // 2. TradingConditions
        TradingConditionsDto conditionsDto = new TradingConditionsDto();
        conditionsDto.setMaximumAccountSizeUsd(entity.getMaxAccountSizeUsd());
        conditionsDto.setProfitSplitPct(entity.getProfitSplit() != null ? entity.getProfitSplit().intValue() : null);
        conditionsDto.setDiscountCode(entity.getDiscountCode());
        // Withdrawal speed is now enum in DTO
        conditionsDto.setWithdrawalSpeed(entity.getWithdrawalSpeed());

        try {
            if (entity.getKeyFeatures() != null) {
                conditionsDto.setKeyFeatures(objectMapper.readValue(
                        entity.getKeyFeatures(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)));
            } else {
                conditionsDto.setKeyFeatures(new ArrayList<>());
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting keyFeatures JSON string to List", e);
        }

        // platforms
        if (entity.getPlatforms() != null) {
            conditionsDto.setTradingPlatforms(new ArrayList<>(entity.getPlatforms()).stream()
                    .map(this::mapEntityToPlatformCode)
                    .collect(Collectors.toList()));
        } else {
            conditionsDto.setTradingPlatforms(new ArrayList<>());
        }

        // assets
        if (entity.getAssets() != null) {
            conditionsDto.setAvailableAssets(new ArrayList<>(entity.getAssets()));
        } else {
            conditionsDto.setAvailableAssets(new ArrayList<>());
        }
        if(includeChallengesAndReviews) {
        // Spreads & Commission
        conditionsDto.setRawSpreads(entity.getRawSpreads());
        conditionsDto.setCommissionPerLot(entity.getCommissionPerLot());

        // Leverages (group FirmLeverage rows into LeverageDto list)
        if (entity.getLeverages() != null && !entity.getLeverages().isEmpty()) {
            Map<LeverageProfile, List<FirmLeverage>> grouped = entity.getLeverages().stream()
                    .collect(Collectors.groupingBy(FirmLeverage::getProfile));
            List<LeverageDto> leverageDtos = new ArrayList<>();
            for (Map.Entry<LeverageProfile, List<FirmLeverage>> e : grouped.entrySet()) {
                LeverageDto ldto = new LeverageDto();
                ldto.setProfile(e.getKey());
                List<InstrumentLeverageDto> instruments = e.getValue().stream()
                        .map(fl -> {
                            InstrumentLeverageDto idto = new InstrumentLeverageDto();
                            idto.setInstrument(fl.getInstrument());
                            idto.setLeverageFactor(fl.getLeverageFactor());
                            return idto;
                        })
                        .collect(Collectors.toList());
                ldto.setInstrumentLeverages(instruments);
                leverageDtos.add(ldto);
            }
            conditionsDto.setLeverages(leverageDtos);
        } else {
            conditionsDto.setLeverages(new ArrayList<>());
        }

        // Payout methods & frequencies
        conditionsDto.setPayoutMethods(new ArrayList<>(entity.getPayoutMethods()));
        conditionsDto.setPayoutFrequencies(new ArrayList<>(entity.getPayoutFrequencies()));

        // News trading rules
        conditionsDto.setNewsTradingRule(entity.getNewsTradingRule());

        // Daily drawdown calculation
        conditionsDto.setDailyDrawdownCalculation(entity.getDailyDrawdownCalculation());

        // Prohibited strategies, restricted countries
        conditionsDto.setProhibitedStrategies(new ArrayList<>(entity.getProhibitedStrategies()));
        conditionsDto.setRestrictedCountries(new ArrayList<>(entity.getRestrictedCountries()));

        // IP / device rules
        conditionsDto.setAllowMultipleDevices(entity.getAllowMultipleDevices());
        conditionsDto.setRequireIpConsistency(entity.getRequireIpConsistency());
        conditionsDto.setConsistencyRuleApplied(entity.getConsistencyRuleApplied());

        // Scaling plan
        conditionsDto.setScalingCriteriaDays(entity.getScalingCriteriaDays());
        conditionsDto.setMaxAllocationAfterScaling(entity.getMaxAllocationAfterScaling());
        conditionsDto.setScalingCycleDays(entity.getScalingCycleDays());
        conditionsDto.setScalingRewardPct(entity.getScalingRewardPct());

        // Support
        conditionsDto.setSupportEmail(entity.getSupportEmail());
        conditionsDto.setLiveChatAvailable(entity.getLiveChatAvailable());
        conditionsDto.setDiscordUrl(entity.getDiscordUrl());
        conditionsDto.setSupportPhone(entity.getSupportPhone());
        
        if (entity.getProfitSplitOptionJson() != null) {
            try {
                ProfitSplitOptionDto ps = objectMapper.readValue(entity.getProfitSplitOptionJson(), ProfitSplitOptionDto.class);
                conditionsDto.setProfitSplitOption(ps);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error parsing profitSplitOption JSON", e);
            }
        } else {
            conditionsDto.setProfitSplitOption(null);
        }


       

        // 3. About
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
        aboutDto.setCeoName(null);
        aboutDto.setExtraNotes(null);
        dto.setAbout(aboutDto);

        // 4. Challenges & Reviews
        dto.setChallenges(challengeCards);
        dto.setReviews(reviews);
        }
        dto.setTradingConditions(conditionsDto);
        return dto;
    }

    // Simple GET
    public FirmResponse toDto(FirmCard entity) {
        return toDto(entity, new ArrayList<>(), new ArrayList<>(),false);
    }

    // --- DTO -> Entity (For POST/PUT operations) ---

    public FirmCard toEntity(FirmRequest request) {
        if (request == null) return null;

        FirmCard entity = new FirmCard();

        // Top-level fields
        entity.setName(request.getName());
        entity.setSlug(request.getSlug());
        entity.setWebsite(request.getWebsite());
        entity.setLogo(request.getLogo());
        String countryCode = request.getCountryCode();
        if (countryCode != null) {
            countryRepository.findByCode(countryCode)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found for code: " + countryCode));
        }
        entity.setCountryCode(countryCode);
        entity.setIsTrusted(request.getIsTrusted());
        entity.setRating(request.getRating());
        entity.setAllRatings(request.getAllRatings());
        entity.setDescription(request.getDescription());
        entity.setUpdated(true);
        entity.setFirmType(request.getFirmType());
        entity.setBuyUrl(request.getFirmPageURL());

        // TradingConditions
        TradingConditionsDto conditions = request.getTradingConditions();
        if (conditions != null) {
            entity.setMaxAccountSizeUsd(conditions.getMaximumAccountSizeUsd());
            entity.setProfitSplit(conditions.getProfitSplitPct() != null ? conditions.getProfitSplitPct().shortValue() : null);
            entity.setDiscountCode(conditions.getDiscountCode());
            // withdrawalSpeed now enum in DTO
            entity.setWithdrawalSpeed(conditions.getWithdrawalSpeed());

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
            } else {
                entity.setPlatforms(new HashSet<>());
            }

            if (conditions.getAvailableAssets() != null) {
                entity.setAssets(new HashSet<>(conditions.getAvailableAssets()));
            } else {
                entity.setAssets(new HashSet<>());
            }

            // Spreads & Commission
            entity.setRawSpreads(conditions.getRawSpreads());
            entity.setCommissionPerLot(conditions.getCommissionPerLot());

            // Leverages -> create FirmLeverage entities
            if (conditions.getLeverages() != null) {
                Set<FirmLeverage> leverages = conditions.getLeverages().stream()
                        .flatMap(ld -> {
                            LeverageProfile profile = ld.getProfile();
                            return ld.getInstrumentLeverages().stream()
                                    .map(il -> {
                                        FirmLeverage fl = new FirmLeverage();
                                        fl.setProfile(profile);
                                        fl.setInstrument(il.getInstrument());
                                        fl.setLeverageFactor(il.getLeverageFactor());
                                        // firmCard will be attached later by linkChildEntities or by service
                                        return fl;
                                    });
                        })
                        .collect(Collectors.toSet());
                // attach set (service should set firmCard when saving or call linkChildEntities)
                entity.setLeverages(leverages);
            } else {
                entity.setLeverages(new HashSet<>());
            }

            // Payouts
            if (conditions.getPayoutMethods() != null) {
                entity.setPayoutMethods(new HashSet<>(conditions.getPayoutMethods()));
            } else {
                entity.setPayoutMethods(new HashSet<>());
            }
            if (conditions.getPayoutFrequencies() != null) {
                entity.setPayoutFrequencies(new HashSet<>(conditions.getPayoutFrequencies()));
            } else {
                entity.setPayoutFrequencies(new HashSet<>());
            }

            entity.setNewsTradingRule(conditions.getNewsTradingRule());
            entity.setDailyDrawdownCalculation(conditions.getDailyDrawdownCalculation());

            if (conditions.getProhibitedStrategies() != null) {
                entity.setProhibitedStrategies(new HashSet<>(conditions.getProhibitedStrategies()));
            } else {
                entity.setProhibitedStrategies(new HashSet<>());
            }

            entity.setAllowMultipleDevices(conditions.getAllowMultipleDevices());
            entity.setRequireIpConsistency(conditions.getRequireIpConsistency());
            entity.setConsistencyRuleApplied(conditions.getConsistencyRuleApplied());

            entity.setScalingCriteriaDays(conditions.getScalingCriteriaDays());
            entity.setMaxAllocationAfterScaling(conditions.getMaxAllocationAfterScaling());
            entity.setScalingCycleDays(conditions.getScalingCycleDays());
            entity.setScalingRewardPct(conditions.getScalingRewardPct());

            if (conditions.getRestrictedCountries() != null) {
                entity.setRestrictedCountries(new HashSet<>(conditions.getRestrictedCountries()));
            } else {
                entity.setRestrictedCountries(new HashSet<>());
            }

            entity.setSupportEmail(conditions.getSupportEmail());
            entity.setLiveChatAvailable(conditions.getLiveChatAvailable());
            entity.setDiscordUrl(conditions.getDiscordUrl());
            entity.setSupportPhone(conditions.getSupportPhone());
            if (conditions.getProfitSplitOption() != null) {
                try {
                    entity.setProfitSplitOptionJson(objectMapper.writeValueAsString(conditions.getProfitSplitOption()));
                } catch (JsonProcessingException e) {
                    throw new RuntimeException("Error serializing profitSplitOption", e);
                }
            } else {
                entity.setProfitSplitOptionJson(null);
            }

        } else {
            entity.setPlatforms(new HashSet<>());
            entity.setAssets(new HashSet<>());
            entity.setLeverages(new HashSet<>());
            entity.setPayoutMethods(new HashSet<>());
            entity.setPayoutFrequencies(new HashSet<>());
            entity.setProhibitedStrategies(new HashSet<>());
            entity.setRestrictedCountries(new HashSet<>());
        }

        // About
        AboutDto about = request.getAbout();
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

        // Ensure child relationships are linked
        linkChildEntities(entity);

        return entity;
    }

    // Looks up the TradingPlatform entity by code and sets it on the Platform join entity.
    private FirmPlatform mapPlatformCodeToEntity(String code) {
        DmnTradingPlatform domainPlatform = tradingPlatformRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Trading platform not found for code: " + code));

        FirmPlatform entity = new FirmPlatform();
        entity.setDomainPlatform(domainPlatform);
        return entity;
    }

    // Helper to map Platform entity back to a code string
    private String mapEntityToPlatformCode(FirmPlatform entity) {
        return entity.getDomainPlatform() != null ? entity.getDomainPlatform().getName() : "UNKNOWN";
    }

    // --- Enum Helpers ---
    // DTO About uses String for firmStatus; keep behavior
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

    public void updateSimpleFields(FirmCard existingEntity, FirmRequest firmDto) {
        // Top-Level
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

        // TradingConditions
        TradingConditionsDto conditions = firmDto.getTradingConditions();
        if (conditions != null) {
            existingEntity.setMaxAccountSizeUsd(conditions.getMaximumAccountSizeUsd());
            existingEntity.setProfitSplit(conditions.getProfitSplitPct() != null ? conditions.getProfitSplitPct().shortValue() : null);
            existingEntity.setDiscountCode(conditions.getDiscountCode());
            existingEntity.setWithdrawalSpeed(conditions.getWithdrawalSpeed());

            try {
                if (conditions.getKeyFeatures() != null) {
                    existingEntity.setKeyFeatures(objectMapper.writeValueAsString(conditions.getKeyFeatures()));
                } else {
                    existingEntity.setKeyFeatures(null);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error converting keyFeatures to JSON string", e);
            }

            if (conditions.getAvailableAssets() != null) {
                existingEntity.setAssets(new HashSet<>(conditions.getAvailableAssets()));
            } else {
                existingEntity.setAssets(new HashSet<>());
            }

            // Commission & spreads
            existingEntity.setRawSpreads(conditions.getRawSpreads());
            existingEntity.setCommissionPerLot(conditions.getCommissionPerLot());

            // Replace leverages: clear and add fresh
            existingEntity.getLeverages().clear();
            if (conditions.getLeverages() != null) {
                Set<FirmLeverage> newLeverages = conditions.getLeverages().stream()
                        .flatMap(ld -> ld.getInstrumentLeverages().stream().map(il -> {
                            FirmLeverage fl = new FirmLeverage();
                            fl.setProfile(ld.getProfile());
                            fl.setInstrument(il.getInstrument());
                            fl.setLeverageFactor(il.getLeverageFactor());
                            fl.setFirmCard(existingEntity);
                            return fl;
                        }))
                        .collect(Collectors.toSet());
                existingEntity.getLeverages().addAll(newLeverages);
            }

            // Payouts/frequencies
            existingEntity.setPayoutMethods(conditions.getPayoutMethods() != null ? new HashSet<>(conditions.getPayoutMethods()) : new HashSet<>());
            existingEntity.setPayoutFrequencies(conditions.getPayoutFrequencies() != null ? new HashSet<>(conditions.getPayoutFrequencies()) : new HashSet<>());

            existingEntity.setNewsTradingRule(conditions.getNewsTradingRule());
            existingEntity.setDailyDrawdownCalculation(conditions.getDailyDrawdownCalculation());

            existingEntity.setProhibitedStrategies(conditions.getProhibitedStrategies() != null ? new HashSet<>(conditions.getProhibitedStrategies()) : new HashSet<>());

            existingEntity.setAllowMultipleDevices(conditions.getAllowMultipleDevices());
            existingEntity.setRequireIpConsistency(conditions.getRequireIpConsistency());
            existingEntity.setConsistencyRuleApplied(conditions.getConsistencyRuleApplied());

            existingEntity.setScalingCriteriaDays(conditions.getScalingCriteriaDays());
            existingEntity.setMaxAllocationAfterScaling(conditions.getMaxAllocationAfterScaling());
            existingEntity.setScalingCycleDays(conditions.getScalingCycleDays());
            existingEntity.setScalingRewardPct(conditions.getScalingRewardPct());

            existingEntity.setRestrictedCountries(conditions.getRestrictedCountries() != null ? new HashSet<>(conditions.getRestrictedCountries()) : new HashSet<>());

            existingEntity.setSupportEmail(conditions.getSupportEmail());
            existingEntity.setLiveChatAvailable(conditions.getLiveChatAvailable());
            existingEntity.setDiscordUrl(conditions.getDiscordUrl());
            existingEntity.setSupportPhone(conditions.getSupportPhone());
        }
    }

    // Update Platform Collection
    public void updatePlatformCollection(FirmCard existingEntity, FirmRequest firmDto) {
        if (existingEntity.getPlatforms() != null) {
            existingEntity.getPlatforms().clear();
        } else {
            existingEntity.setPlatforms(new HashSet<>());
        }

        List<String> platformCodes = firmDto.getTradingConditions() != null ? firmDto.getTradingConditions().getTradingPlatforms() : null;

        if (platformCodes != null) {
            Set<FirmPlatform> newPlatforms = platformCodes.stream()
                    .map(this::mapPlatformCodeToEntity)
                    .peek(p -> p.setFirmCard(existingEntity))
                    .collect(Collectors.toSet());
            existingEntity.getPlatforms().addAll(newPlatforms);
        }
    }

    // Links Platforms & Leverages back to the FirmCard after mapping
    public void linkChildEntities(FirmCard entity) {
        if (entity.getPlatforms() != null) {
            entity.getPlatforms().forEach(p -> p.setFirmCard(entity));
        }
        if (entity.getLeverages() != null) {
            entity.getLeverages().forEach(fl -> fl.setFirmCard(entity));
        }
    }
}
