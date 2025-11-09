package com.tps.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FirmFilterOptionsDto {
    private List<FirmCategoryDto> firmCategories;
    private List<DropdownOptionDto> challengePhases; // Renamed from phaseTypes
    private List<DropdownOptionDto> sortOptions;   
    private List<DropdownOptionDto> minAcctSize;    
}