package com.tps.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "firm_phases")
public class Phase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="min_trading_days")
    private int minTradingDays;

    private int phase;
    
    @Column(name="profit_target_pct")
    private int profitTargetPct;
    
    
    
    @Column(name="time_limit_days")
    private int timeLimitDays;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    @ToString.Exclude 
    @EqualsAndHashCode.Exclude 
    private Challenge challenge;
}