package com.tps.dto;

import java.math.BigDecimal;
import java.util.List;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class Firm {
	
	 private Long id; 
	 
	 @NotBlank(message = "Title is required")
	 @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
	  private String title;
	  
	 @Min(value = 0, message = "Profit split cannot be negative")
	  private Integer profitSplit;
	  
	 @NotNull(message = "Account size is required")
	 @Min(value = 0, message = "Account size must be a positive number")
	  private BigDecimal account;
	  
	  private String code;
	  private String logo;
	  private boolean updated;
	  private String rating;
	  private Integer allRatings;
	  private String country;
	  private String flag;
	  
	  @NotNull(message = "userId is required for auditing")
      private Long userId;
	 

	  
	  @NotNull(message = "Assets list cannot be null (can be empty)")
	  private List<String> assets;
	  
	  @Valid 
	  @NotNull(message = "Platforms list cannot be null")
	  @Size(min = 1, message = "At least one platform is required")
	  private List<Platform> platforms;
	  
	 @NotNull(message = "Max allocation is required")
	 @Min(value = 0, message = "Max allocation must be a positive number")
	  private BigDecimal maxAllocation;
	  
	  
}