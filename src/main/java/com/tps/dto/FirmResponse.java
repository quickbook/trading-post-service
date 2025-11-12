package com.tps.dto;

 
 
import java.util.List; 
import lombok.Data;

@Data
public class FirmResponse {
	
	 private Long id; 	 
	 private String name;
	 private String slug;
	 private String rating;
	 private String logo;
	 private String firmPageURL;
	 private String firmType;
	 private Integer allRatings;
	 private String country;
	 private String countryCode;
	 private TradingConditionsDto tradingConditions;
	 private AboutDto about;
     private List<ChallengeCardDto> challenges;
     private List<FirmReviewDto> reviews;


	 private String description;	 

	 private String website;
	 private Boolean isTrusted;	
}