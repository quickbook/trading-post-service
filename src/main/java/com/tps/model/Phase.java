package com.tps.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data; 

@Data
@Entity
@Table(name = "firm_phases")
public class Phase {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    private int phase; 
    
    private int profitTargetPct;
    private int minTradingDays;
    private int timeLimitDays;

    
    @ManyToOne(fetch = FetchType.LAZY)
    private Challenge challenge; // Ensure this type is the Entity
}