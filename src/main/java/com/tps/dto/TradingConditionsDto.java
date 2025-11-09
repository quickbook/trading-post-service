package com.tps.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradingConditionsDto {
    private Integer maximumAccountSizeUsd;
    private Integer profitSplitPct;
    private String discountCode;
    private String withdrawalSpeed; // Enum as String (e.g., "Same Day")
    private List<String> keyFeatures; // Stored as List of Strings
    private List<String> tradingPlatforms; // List of platform codes (e.g., "MT5")
    private List<String> availableAssets;  // List of asset codes (e.g., "FX")
}