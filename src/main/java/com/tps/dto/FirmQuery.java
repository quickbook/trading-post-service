package com.tps.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class FirmQuery {
    private Long categoryId;
    private Long phaseTypeId;

    @Min(0)
    private BigDecimal minAccount; // e.g., 5000

    private String country;
    private Boolean updated; // maps to your bit(1) column
}