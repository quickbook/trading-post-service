package com.tps.dto;

import java.time.Instant;
import lombok.Data;

@Data
public class FirmReviewDto {
	
    private Long id;
    private Long firmId;
    private String reviewerName;
    private String propName;
    private String rating;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
    private Boolean isDeleted;
}