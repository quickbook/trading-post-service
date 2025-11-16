package com.tps.dto.response;

 
 
import java.util.List;

import com.tps.dto.AboutDto;
import com.tps.dto.TradingConditionsDto;

import lombok.Data;

@Data
public class FirmResponse {
	
	 private Long id; 	 
	 private String name;
	 private String slug;
	 private String rating;
	 private String logo;
	 //private String firmPageURL;
	 private String buyUrl;
	 private String firmType;
	 private Integer allRatings;
	 private String country;
	 private String countryCode;
	 private TradingConditionsDto tradingConditions;
	 private AboutDto about;
     private List<ChallengeCardDto> challenges;
     private List<ReviewResponse> reviews;


	 private String description;	 

	 private String website;
	 private Boolean isTrusted;	
}