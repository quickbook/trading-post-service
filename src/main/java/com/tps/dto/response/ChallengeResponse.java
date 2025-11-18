package com.tps.dto.response;

import java.math.BigDecimal;

import com.tps.dto.PriceDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeResponse {
	
	private Long id;
	private Long firmId;
	private String firmName;
	private String logo;
	private String buyUrl;
    private String tier;
    private String phase;
    private BigDecimal profitTargetPct;
    private BigDecimal dailyLossPct;
    private BigDecimal maxLossPct;
    private Integer accountSizeUsd;
    private PriceDto price;  
}
