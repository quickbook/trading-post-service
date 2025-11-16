package com.tps.dto; 

import com.tps.enums.InstrumentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstrumentLeverageDto {

    @NotNull(message = "Instrument is required")
    private InstrumentType instrument;

    /**
     * Represent leverage as numeric factor.
     * Example: 100 -> 1:100, 2 -> 1:2
     */
    @NotNull(message = "Leverage factor is required")
    @Min(value = 1, message = "Leverage factor must be >= 1")
    private Integer leverageFactor;
}
