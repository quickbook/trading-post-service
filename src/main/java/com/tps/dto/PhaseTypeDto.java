package com.tps.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhaseTypeDto {
    private Long id;
    private String key;
    private String label;
}
