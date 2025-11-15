package com.tps.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AboutDto {
    /**
     * Full legal name of the company.
     * Example: Alpha Trader Firm LLC
     */
    @Size(max = 150, message = "Legal name must be <= 150 characters")
    private String legalName;

    /**
     * Registration number — alphanumeric.
     * Examples: 2025-001626598
     */
    @Size(max = 50, message = "Registration number must be <= 50 characters")
    private String registrationNo;

    /**
     * Established date — must be in the past.
     * Example: 2022-07-01
     */
    @Past(message = "Established date must be in the past")
    private LocalDate establishedDate;

    /**
     * Founders name(s)
     */
    @Size(max = 150, message = "Founders field must be <= 150 characters")
    private String founders;

    /**
     * Headquarters address (City, Country).
     */
    @Size(max = 150, message = "Headquarters must be <= 150 characters")
    private String headquarters;

    /**
     * Jurisdiction (Country or region).
     */
    @Size(max = 150, message = "Jurisdiction must be <= 150 characters")
    private String jurisdiction;

    /**
     * Overview / description about the firm.
     */
    @Size(max = 2000, message = "Description must be <= 2000 characters")
    private String description;

    /**
     * Firm status — domain driven (Active, Paused, etc.)
     */
    @NotBlank(message = "Firm status is required")
    private String firmStatus;

    /**
     * Company founding year
     * Example: 2022, 2019, etc.
     */
    @Min(value = 1900, message = "Founded year must be >= 1900")
    @Max(value = 2100, message = "Founded year must be <= 2100")
    private Short foundedYear; 
}