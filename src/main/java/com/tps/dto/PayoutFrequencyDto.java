package com.tps.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayoutFrequencyDto {
    private Long id;
    private String code;
    private String label;
    private String description;
}