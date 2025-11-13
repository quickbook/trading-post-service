package com.tps.dto;

import java.time.Instant;
import lombok.Data;

@Data
public class FirmReviewDto {
	
    private Long id;
    private Long firmId;
    private String reviewerName;
    private String propName;
    private String tradingExp;
    private String rating;
    private Boolean isVrfdPurchase;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isDeleted;
}