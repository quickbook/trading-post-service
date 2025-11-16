package com.tps.mapper;

import com.tps.dto.PriceDto;
import com.tps.dto.request.ChallengeRequest; 
import com.tps.dto.response.ChallengeResponse;
import com.tps.exceptions.ResourceNotFoundException;
import com.tps.model.DmnChallengePhase; 
import com.tps.model.DmnTier; 
import com.tps.model.FirmCard; 
import com.tps.model.FirmChallenge; 
import com.tps.repository.ChallengePhaseRepository;
import com.tps.repository.FirmRepository;
import com.tps.repository.TierRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component; 

@Component
@RequiredArgsConstructor
public class ChallengeMapper {

    private final FirmRepository firmRepository;
    private final TierRepository tierRepository;
    private final ChallengePhaseRepository challengePhaseRepository;


    // --- 1. Entity -> DTO (Output Mapping) ---
    public ChallengeResponse toResponseDto(FirmChallenge entity) {
        if (entity == null) return null;

        PriceDto price = new PriceDto(entity.getPriceAmount(), entity.getPriceCurrency());

        return new ChallengeResponse(
            entity.getId(),
            entity.getTier().getName(),       
            entity.getPhase().getLabel(),        
            entity.getProfitTargetPct(),
            entity.getDailyLossPct(),
            entity.getMaxLossPct(),
            entity.getAccountSizeUsd(),
            price
        );
    }
    
    // --- 2. DTO -> Entity (Input Mapping for Create/Update) ---
    public FirmChallenge toEntity(ChallengeRequest request) {
        if (request == null) return null;

        FirmChallenge entity = new FirmChallenge();
        
        entity.setFirmCard(getFirmCardReference(request.getFirmId())); // Requires firmId in ChallengeRequest
        entity.setTier(getDmnTierReference(request.getDmnTierId()));
        entity.setPhase(getDmnPhaseReference(request.getDmnPhaseId()));

        // Map Price & Metrics Fields
        entity.setPriceAmount(request.getPrice().getAmount());
        entity.setPriceCurrency(request.getPrice().getCurrency());
        entity.setProfitTargetPct(request.getProfitTargetPct());
        entity.setDailyLossPct(request.getDailyLossPct());
        entity.setMaxLossPct(request.getMaxLossPct());
        entity.setAccountSizeUsd(request.getAccountSizeUsd());

        return entity;
    }
    
    // --- Helper Methods for Foreign Key Resolution ---

    private FirmCard getFirmCardReference(Long firmId) {
        return firmRepository.findById(firmId)
                .orElseThrow(() -> new ResourceNotFoundException("Firm not found with id: " + firmId));
    }

    private DmnTier getDmnTierReference(Integer tierId) {
        // Assuming DmnTier IDs are Longs, casting the request's Integer ID
        return tierRepository.findById(Long.valueOf(tierId)) 
                .orElseThrow(() -> new ResourceNotFoundException("Tier not found with id: " + tierId));
    }

    private DmnChallengePhase getDmnPhaseReference(Integer phaseId) {
        // Assuming DmnChallengePhase IDs are Longs
        return challengePhaseRepository.findById(Long.valueOf(phaseId))
                .orElseThrow(() -> new ResourceNotFoundException("Challenge Phase not found with id: " + phaseId));
    }
}