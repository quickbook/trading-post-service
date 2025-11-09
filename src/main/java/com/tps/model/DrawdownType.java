package com.tps.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "dmn_drawdown_types")
@Data
public class DrawdownType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
    private String code; // 'BALANCE', 'EQUITY', 'TRAILING'

    @Column(nullable = false, length = 64)
    private String label;

    @Column(length = 255)
    private String description;
}