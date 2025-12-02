package com.tps.dto;

import java.util.List;

import com.tps.enums.LeverageProfile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeverageDto {
	
	private Long id;

    @NotNull(message = "Leverage profile is required")
    private LeverageProfile profile;

    @NotEmpty(message = "At least one instrument leverage must be provided")
    @Valid
    private List<InstrumentLeverageDto> instrumentLeverages;
}
