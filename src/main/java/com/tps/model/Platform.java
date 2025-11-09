package com.tps.model;

import jakarta.persistence.Column; // NEW IMPORT
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "firm_platforms", indexes = {
    @Index(name = "idx_platform_firm_id", columnList = "firm_id"),
    @Index(name = "idx_platform_domain_id", columnList = "platform_id")
})
public class Platform {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", columnDefinition = "BIGINT UNSIGNED") 
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id", nullable = false)
    private TradingPlatform domainPlatform;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firm_id", columnDefinition = "BIGINT UNSIGNED") 
    @ToString.Exclude 
    @EqualsAndHashCode.Exclude 
    private FirmCard firmCard;
}