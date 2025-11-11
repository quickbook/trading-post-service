package com.tps.dto;

 
import java.util.List;
 

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
	 private String rating;
	 private String website;
	 private String logo;
	 private String firmPageURL;
	 private String firmType;
	 private Integer allRatings;
	 private String country;
	 private String countryCode;
	 private String description;	 
	 private Boolean isTrusted;
	 private String offerCode;
	 private String buyUrl;
	 
	 private TradingConditionsDto tradingConditions;
	 private AboutDto about;
     private List<ChallengeCardDto> challenges;
     private List<FirmReviewDto> reviews;
}