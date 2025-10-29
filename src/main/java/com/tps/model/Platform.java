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


@Data
@Entity
@Table(name = "firm_platforms", indexes = {
	    @Index(name = "idx_platform_firmcard_id", columnList = "firmCard_id") // Index the FK column
	}) // Separate table to store the Platform Information


public class Platform{
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String src; 
    private String alt; 
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firmCard_id") //We link the this table to Main FirmCard Table
    private FirmCard firmCard;
}