package com.tps.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tps.dto.CountryDto;
import com.tps.dto.CurrencyDto;
import com.tps.dto.ChallengePhaseFullDto;
import com.tps.dto.DrawdownTypeDto;
import com.tps.dto.InstrumentDto;
import com.tps.dto.PayoutFrequencyDto;
import com.tps.dto.TierDto;
import com.tps.dto.TradingPlatformDto;
import com.tps.dto.RoleDto;
import com.tps.repository.ChallengePhaseRepository;
import com.tps.repository.CountryRepository;
import com.tps.repository.CurrencyRepository;
import com.tps.repository.DrawdownTypeRepository;
import com.tps.repository.InstrumentRepository;
import com.tps.repository.PayoutFrequencyRepository;
import com.tps.repository.RoleRepository;
import com.tps.repository.TierRepository;
import com.tps.repository.TradingPlatformRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DomainService {

    private final CountryRepository countryRepository;
    private final RoleRepository roleRepository;
    
    private final TradingPlatformRepository tradingPlatformRepository;
    private final InstrumentRepository instrumentRepository;
    private final TierRepository tierRepository;
    private final ChallengePhaseRepository challengePhaseRepository;
    private final DrawdownTypeRepository drawdownTypeRepository;
    private final PayoutFrequencyRepository payoutFrequencyRepository;
    private final CurrencyRepository currencyRepository;


    
    public List<CountryDto> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(country -> new CountryDto(country.getCode(), country.getName()))
                .collect(Collectors.toList());
    }
    
    

    
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleDto(role.getId(), role.getName()))
                .collect(Collectors.toList());
    }
    
    // --- New Domain Data Methods (Using specific DTOs) ---

    public List<TradingPlatformDto> getAllTradingPlatforms() {
        return tradingPlatformRepository.findAll().stream()
                // Mapped to new 2-argument constructor
                .map(p -> new TradingPlatformDto(p.getCode(), p.getName())) 
                .collect(Collectors.toList());
    }
    
    public List<InstrumentDto> getAllInstruments() {
        return instrumentRepository.findAll().stream()
                // Mapped to new 2-argument constructor
                .map(i -> new InstrumentDto(i.getCode(), i.getName()))
                .collect(Collectors.toList());
    }
    
    public List<TierDto> getAllTiers() {
        return tierRepository.findAll().stream()
                // Mapped to new 1-argument constructor
                .map(t -> new TierDto(t.getName()))
                .collect(Collectors.toList());
    }
    
    
    
    public List<ChallengePhaseFullDto> getAllChallengePhases() {
        // Mapped to new 2-argument constructor
        return challengePhaseRepository.findAllByOrderByCodeAsc().stream() 
               .map(c -> new ChallengePhaseFullDto(c.getCode(), c.getLabel()))
               .collect(Collectors.toList());
   }
    
    
    public List<DrawdownTypeDto> getAllDrawdownTypes() {
        return drawdownTypeRepository.findAll().stream()
                .map(d -> new DrawdownTypeDto(d.getId(), d.getCode(), d.getLabel(), d.getDescription()))
                .collect(Collectors.toList());
    }
    
    public List<PayoutFrequencyDto> getAllPayoutFrequencies() {
        return payoutFrequencyRepository.findAll().stream()
                .map(p -> new PayoutFrequencyDto(p.getId(), p.getCode(), p.getLabel(), p.getDescription()))
                .collect(Collectors.toList());
    }
    
    
    
    public List<CurrencyDto> getAllCurrencies() {
        return currencyRepository.findAll().stream()
                .map(c -> new CurrencyDto(c.getCode(), c.getName(), c.getSymbol()))
                .collect(Collectors.toList());
    }
}