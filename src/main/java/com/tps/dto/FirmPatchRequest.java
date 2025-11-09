package com.tps.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.tps.model.FirmStatus; // Import FirmStatus
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FirmPatchRequest {

    @Size(min = 3, max = 150, message = "Name must be between 3 and 150 characters")
    private String name;
    
    private String slug; 
    private BigDecimal rating;
    private Integer allRatings;
    
    private Integer maxAccountSizeUsd;
    private Short profitSplitPct;
    private String discountCode;
    private String withdrawalSpeed; 
    private String keyFeatures; 
    
    private String legalName;
    private String registrationNo;
    private LocalDate establishedDate;
    
    private FirmStatus firmStatus; // Enum
    
    private String description;
    
    @NotNull(message = "userId is required for auditing")
    private Long userId;

    private Boolean updated;
}