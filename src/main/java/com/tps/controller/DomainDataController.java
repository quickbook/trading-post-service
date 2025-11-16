package com.tps.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tps.dto.CountryDto;
import com.tps.dto.CurrencyDto;
import com.tps.dto.ChallengePhaseFullDto;
import com.tps.dto.DrawdownTypeDto;
import com.tps.dto.InstrumentDto;
import com.tps.dto.PayoutFrequencyDto;
import com.tps.dto.RoleDto;
import com.tps.dto.TierDto;
import com.tps.dto.response.ApiResponse;
import com.tps.dto.response.TradingPlatformDto;
import com.tps.service.DomainService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tradingpost/api/v1/domain")
@RequiredArgsConstructor
public class DomainDataController {

    private final DomainService domainService;

    @GetMapping("/countries")
    public ResponseEntity<ApiResponse<List<CountryDto>>> getAllCountries(HttpServletRequest request) {
        
        List<CountryDto> countries = domainService.getAllCountries();
        
        return ResponseEntity.ok(
            ApiResponse.<List<CountryDto>>builder()
                .success(true).message("Countries fetched successfully").data(countries)
                .status(HttpStatus.OK).path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<RoleDto>>> getAllRoles(HttpServletRequest request) {
        
        List<RoleDto> roles = domainService.getAllRoles();
        
        return ResponseEntity.ok(
            ApiResponse.<List<RoleDto>>builder()
                .success(true).message("Roles fetched successfully").data(roles)
                .status(HttpStatus.OK).path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }
    
    // --- NEW DOMAIN ENDPOINTS (Using specific DTOs) ---
    
    @GetMapping("/platforms")
    public ResponseEntity<ApiResponse<List<TradingPlatformDto>>> getAllTradingPlatforms(HttpServletRequest request) {
        List<TradingPlatformDto> platforms = domainService.getAllTradingPlatforms();
        return ResponseEntity.ok(
            ApiResponse.<List<TradingPlatformDto>>builder()
                .success(true).message("Trading platforms fetched successfully").data(platforms)
                .status(HttpStatus.OK).path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }
    
    @GetMapping("/instruments")
    public ResponseEntity<ApiResponse<List<InstrumentDto>>> getAllInstruments(HttpServletRequest request) {
        List<InstrumentDto> instruments = domainService.getAllInstruments();
        return ResponseEntity.ok(
            ApiResponse.<List<InstrumentDto>>builder()
                .success(true).message("Instruments fetched successfully").data(instruments)
                .status(HttpStatus.OK).path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }
    
    @GetMapping("/tiers")
    public ResponseEntity<ApiResponse<List<TierDto>>> getAllTiers(HttpServletRequest request) {
        List<TierDto> tiers = domainService.getAllTiers();
        return ResponseEntity.ok(
            ApiResponse.<List<TierDto>>builder()
                .success(true).message("Tiers fetched successfully").data(tiers)
                .status(HttpStatus.OK).path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }
    
    @GetMapping("/challenge-phases")
    public ResponseEntity<ApiResponse<List<ChallengePhaseFullDto>>> getFullChallengePhases(HttpServletRequest request) {
        List<ChallengePhaseFullDto> phases = domainService.getAllChallengePhases();
        return ResponseEntity.ok(
            ApiResponse.<List<ChallengePhaseFullDto>>builder()
                .success(true).message("Challenge phases fetched successfully").data(phases)
                .status(HttpStatus.OK).path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }
    
    @GetMapping("/drawdown-types")
    public ResponseEntity<ApiResponse<List<DrawdownTypeDto>>> getAllDrawdownTypes(HttpServletRequest request) {
        List<DrawdownTypeDto> drawdownTypes = domainService.getAllDrawdownTypes();
        return ResponseEntity.ok(
            ApiResponse.<List<DrawdownTypeDto>>builder()
                .success(true).message("Drawdown types fetched successfully").data(drawdownTypes)
                .status(HttpStatus.OK).path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }
    
    @GetMapping("/payout-frequencies")
    public ResponseEntity<ApiResponse<List<PayoutFrequencyDto>>> getAllPayoutFrequencies(HttpServletRequest request) {
        List<PayoutFrequencyDto> frequencies = domainService.getAllPayoutFrequencies();
        return ResponseEntity.ok(
            ApiResponse.<List<PayoutFrequencyDto>>builder()
                .success(true).message("Payout frequencies fetched successfully").data(frequencies)
                .status(HttpStatus.OK).path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }
    
    @GetMapping("/currencies")
    public ResponseEntity<ApiResponse<List<CurrencyDto>>> getAllCurrencies(HttpServletRequest request) {
        List<CurrencyDto> currencies = domainService.getAllCurrencies();
        return ResponseEntity.ok(
            ApiResponse.<List<CurrencyDto>>builder()
                .success(true).message("Currencies fetched successfully").data(currencies)
                .status(HttpStatus.OK).path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }
}