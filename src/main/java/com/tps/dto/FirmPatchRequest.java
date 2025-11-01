package com.tps.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FirmPatchRequest {

    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String title;

    @Min(value = 0, message = "Profit split cannot be negative")
    private Integer profitSplit;

    @Min(value = 0, message = "Account size must be a positive number")
    private BigDecimal account;

    private String code;
    private String logo;
    private String rating;
    private Integer allRatings;
    private String country;
    private String flag;
    @NotNull(message = "userId is required for auditing")
    private Long userId;

    @Min(value = 0, message = "Max allocation must be a positive number")
    private BigDecimal maxAllocation;
    
    // Note: We use Boolean (the object) instead of boolean (the primitive)
    // so that it can be null if not provided.
    private Boolean updated;
}