package com.tps.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for structuring individual field validation errors.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorDto {
    private String field;
    private String message;
}