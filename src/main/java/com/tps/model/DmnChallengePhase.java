package com.tps.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "dmn_challenge_phases")
@Data
public class DmnChallengePhase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 32)
    private String code; // e.g., 'PHASE_1', 'PHASE_2'

    @Column(nullable = false, length = 64)
    private String label; // e.g., 'Phase 1'

    @Column(length = 255)
    private String description;
}