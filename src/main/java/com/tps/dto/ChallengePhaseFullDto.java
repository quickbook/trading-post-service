package com.tps.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChallengePhaseFullDto {
	private Long id;
    private String code;
    private String label;
}