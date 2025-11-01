package com.tps.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateReviewRequest {

    @NotBlank(message = "Reviewer name is required")
    private String reviewerName;

    private String propName;
    
    @NotBlank(message = "Rating is required")
    private String rating;

    @NotBlank(message = "Description is required")
    private String description;
}