package com.tps.model;

import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable; // Marks the entity as read-only

@Entity
@Immutable
@Table(name = "v_challenge_cards")
@Data
public class ChallengeCardView {

    @Id
    @Column(name = "plan_id")
    private Long planId;
    
    @Column(name = "firm_id")
    private Long firmId;
    
    @Column(name = "firm_name")
    private String firmName;
    
    @Column(name = "firm_slug")
    private String firmSlug;
    
    @Column(name = "firm_logo")
    private String firmLogo;
    
    @Column(name = "tier_name")
    private String tierName;
    
    @Column(name = "phase_label")
    private String phaseLabel;
    
    @Column(name = "profit_target_pct")
    private BigDecimal profitTargetPct;
    
    @Column(name = "daily_loss_pct")
    private BigDecimal dailyLossPct;
    
    @Column(name = "max_loss_pct")
    private BigDecimal maxLossPct;
    
    @Column(name = "account_size_usd")
    private Integer accountSizeUsd;
    
    @Column(name = "price_amount")
    private BigDecimal priceAmount;
    
    @Column(name = "price_currency")
    private String priceCurrency;
    
    @Column(name = "buy_url")
    private String buyUrl;
}