package com.tps.dto;

 
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FirmPatchRequest {
	 private Long id; 
	 
	 @NotNull
	 @NotBlank(message = "Name is required")
	 @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
	 private String name;
	 
	 private String slug;
	 
	 @Pattern(
			 	regexp = "A\\+|A|B|C|D",
			 	message = "Rating must be one of: A+, A, B, C, D"
			 )
	 private String rating;
	 
	 @Pattern(
			    regexp = "^(https?://).+",
			    message = "Website must be a valid URL starting with http:// or https://"
			)
	 private String website;
	 private String logo;
	 
	 @Pattern(
			    regexp = "^(https?://).+",
			    message = "Firm Page URL must be a valid URL"
			)
	 private String firmPageURL;
	 
	 @Size(max = 50, message = "Firm type cannot exceed 50 characters")
	 private String firmType;
	 
	 @Min(value = 0, message = "All ratings must be non-negative")
	 private Integer allRatings;
	 
	 @Pattern(
			    regexp = "^[A-Z]{2,3}$",
			    message = "Country code must be 2 or 3 uppercase letters (e.g., US, UK, IND)"
			)
	 private String countryCode;
	 private String description;
	 
	 @NotNull(message = "Trusted field must be specified")
	 private Boolean isTrusted; 
	 
	 @Valid
	 private TradingConditionsDto tradingConditions;
	 
	 @Valid
	 private AboutDto about;
     private List<ChallengeCardDto> challenges;
     private List<FirmReviewDto> reviews;
}