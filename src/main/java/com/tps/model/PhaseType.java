package com.tps.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "phase_types")
@Data
public class PhaseType {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String key;     // "ALL", "ONE", "TWO", "INSTANT"
    private String label;   // "All Phases", "One Phase", etc.
    private Integer steps;  // optional: 1, 2, or 0 for instant
    private Boolean active = true;
}
