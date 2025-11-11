package com.tps.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeCardDto {
	
	private Long id;
    private String tier;
    private String phase;
    private BigDecimal profitTargetPct;
    private BigDecimal dailyLossPct;
    private BigDecimal maxLossPct;
    private Integer accountSizeUsd;
    private PriceDto price; // Nested PriceDto
    private String buyUrl;
}