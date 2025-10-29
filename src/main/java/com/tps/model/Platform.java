package com.tps.model;

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
    @Index(name = "idx_platform_firmcard_id", columnList = "firm_card_id")
})
public class Platform {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String alt;

    
    private String src;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firm_card_id")
    @ToString.Exclude 
    @EqualsAndHashCode.Exclude 
    private FirmCard firmCard;
}