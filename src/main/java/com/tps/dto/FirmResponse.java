package com.tps.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import jakarta.validation.Valid;

import lombok.Data;

@Data
public class FirmResponse {
	
	 private Long id; 
	 
	 private String name;
	 private String slug;
	 private String website;
	 private String logoUrl;
	 private String hqCountry;
	 private Short foundedYear;
	 
	 private Boolean isTrusted;
	 private BigDecimal rating;
	 private Integer allRatings;
	 private String description;

	  
	 private Instant createdAt;
	 private Instant updatedAt;
	  
	 private Long createdBy; 
     private Long updatedBy;

	 // Nested DTOs
	 private TradingConditionsDto tradingConditions;
	 private AboutDto about;
	 
	 // Nested Challenge Cards (List of available challenges/plans)
	 @Valid
     private List<ChallengeCardDto> challenges;
}