package com.tps.dto.request;

import java.math.BigDecimal;

import com.tps.dto.PriceDto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data; 
@Data
public class ChallengeRequest {

    @NotNull(message = "Tier ID is required")
    @Positive(message = "Tier ID must be a positive number")
    private Integer dmnTierId;

    @NotNull(message = "Phase ID is required")
    @Positive(message = "Phase ID must be a positive number")
    private Integer dmnPhaseId;

    @NotNull(message = "Profit Target % is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Profit Target % must be greater than 0")
    private BigDecimal profitTargetPct;

    @NotNull(message = "Daily Loss % is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Daily Loss % must be greater than 0")
    private BigDecimal dailyLossPct;

    @NotNull(message = "Max Loss % is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Max Loss % must be greater than 0")
    private BigDecimal maxLossPct;

    @NotNull(message = "Account size is required")
    @Positive(message = "Account size must be a positive number")
    private Integer accountSizeUsd;

    @NotNull(message = "Price details are required")
    @Valid  // IMPORTANT — validates nested DTO
    private PriceDto price;
}
