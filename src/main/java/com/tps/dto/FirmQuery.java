package com.tps.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class FirmQuery {
    private Long categoryId;
    private Long phaseTypeId;

    @Min(0)
    private BigDecimal minAccount; // Now filters FirmCard.maxAccountSizeUsd

    private String country; // Now filters FirmCard.hqCountry
    private Boolean updated; 
}