package com.tps.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateReviewRequest {

    @NotBlank(message = "Reviewer name is required")
    private String reviewerName;
    
    @NotNull(message = "Firm Id is required")
    private Long firmId;
 
    private String propName;
    
    @NotBlank(message = "Rating is required")
    private String rating;

    @NotBlank(message = "Description is required")
    private String description;
}