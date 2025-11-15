package com.tps.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateReviewRequest {
	
 
	@NotBlank(message = "Trading Experience is required")
	@Pattern(
	    regexp = "BEGINNER|INTERMEDIATE|ADVANCED|EXPERT|PROFESSIONAL",
	    message = "Trading Experience must be one of: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT, PROFESSIONAL"
	)
	private String tradingExp;


	@NotBlank(message = "Rating is required")
	@Pattern(
	    regexp = "A\\+|A|B|C|D",
	    message = "Rating must be one of: A+, A, B, C, D"
	)
	private String rating;

	@NotBlank(message = "Description is required")
	@Size(max = 2000, message = "Description must be <= 2000 characters")
	private String description;

}