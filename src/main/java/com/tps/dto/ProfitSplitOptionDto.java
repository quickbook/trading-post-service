package com.tps.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfitSplitOptionDto {
    @Min(value = 0, message = "Initial split must be >= 0")
    @Max(value = 100, message = "Initial split must be <= 100")
    private Integer initialPct;         // e.g., 80

    @Min(value = 0, message = "Subsequent split must be >= 0")
    @Max(value = 100, message = "Subsequent split must be <= 100")
    private Integer subsequentPct;      // e.g., 100

    @Size(max = 100, message = "Notes must be <= 100 chars")
    private String note;    
}
