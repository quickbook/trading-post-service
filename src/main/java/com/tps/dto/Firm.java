package com.tps.dto;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class Firm {
	
	 private Long id; 
	 
	 @NotBlank(message = "Firm name is required")
	 @Size(min = 3, max = 150, message = "Name must be between 3 and 150 characters")
	 private String name;
	 
	 @NotBlank(message = "Slug is required")
	 private String slug;
	 
	 @NotBlank(message = "Website is required")
	 private String website;
	 
	 private String logoUrl;
	 private String hqCountry;
	 private Short foundedYear;
	 
	 private Boolean isTrusted = false;
	 private BigDecimal rating;
	 private Integer allRatings;
	 
	 @NotNull(message = "UserId is required for auditing")
     private Long userId; // For auditing

     private String description;
	 
	 // Nested DTOs
	 @Valid
	 @NotNull(message = "Trading conditions are required")
	 private TradingConditionsDto tradingConditions;
	 
	 @Valid
	 @NotNull(message = "About section is required")
	 private AboutDto about;
}