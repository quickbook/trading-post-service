package com.tps.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "risk_profiles")
@Data
public class RiskProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(name = "max_daily_drawdown_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxDailyDrawdownPct;

    @Column(name = "max_overall_drawdown_pct", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxOverallDrawdownPct;
}