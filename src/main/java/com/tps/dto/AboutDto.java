package com.tps.dto;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AboutDto {
    private String legalName;
    private String registrationNo;
    private LocalDate establishedDate;
    private String founders;
    private String headquarters;
    private String jurisdiction;
    private String firmStatus; // Enum as String (e.g., "Active")
}