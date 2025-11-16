package com.tps.dto;

import java.util.List;

import com.tps.enums.WithdrawalSpeedEnum;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradingConditionsDto {

    @Min(value = 1, message = "Maximum account size must be greater than 0")
    private Integer maximumAccountSizeUsd;

    @Min(value = 0, message = "Profit split cannot be negative")
    @Max(value = 100, message = "Profit split cannot exceed 100%")
    private Integer profitSplitPct;

    @Pattern(
        regexp = "^[A-Za-z0-9]{2,20}$",
        message = "Discount code must be 2–20 alphanumeric characters"
    )
    private String discountCode;

    /**
     * Withdrawal speed — ENUM (not validated now because domain driven)
     */
 
    @NotBlank(message = "WithdrawalSpeed is required")
    private String withdrawalSpeed;

    /**
     * Key Features — structure only (non empty), values validated in service
     */
    @Size(max = 20, message = "Maximum of 20 key features allowed")
    private List<
        @NotBlank(message = "Feature cannot be empty")
        @Size(max = 100, message = "Feature must be max 100 characters")
        String
    > keyFeatures;

    /**
     * Trading platforms — no patterns because domain-driven
     */
    @NotEmpty(message = "At least one trading platform is required")
    private List<String> tradingPlatforms;

    /**
     * Available assets — domain-driven list
     */
    @NotEmpty(message = "At least one asset is required")
    private List<String> availableAssets;
}