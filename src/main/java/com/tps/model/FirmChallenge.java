package com.tps.model;

import java.math.BigDecimal;
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

@Data
@Entity
@Table(name = "firm_challenge") 
public class FirmChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link back to the FirmCard
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firm_card_id", nullable = false)
    private FirmCard firmCard; 

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tier_id", nullable = false)
    private DmnTier tier; 
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phase_id", nullable = false)
    private DmnChallengePhase phase; 

    
    @Column(name = "profit_target_pct", nullable = false)
    private BigDecimal profitTargetPct;

    @Column(name = "daily_loss_pct", nullable = false)
    private BigDecimal dailyLossPct;

    @Column(name = "max_loss_pct", nullable = false)
    private BigDecimal maxLossPct;

    @Column(name = "account_size_usd", nullable = false)
    private Integer accountSizeUsd;

    
    @Column(name = "price_amount", nullable = false)
    private BigDecimal priceAmount;

    @Column(name = "price_currency", nullable = false, length = 10)
    private String priceCurrency;
}