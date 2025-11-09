package com.tps.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "dmn_currencies")
@Data
public class Currency {
    
    @Id 
    @Column(length = 3, nullable = false)
    private String code; // e.g., 'USD', 'EUR'

    @Column(nullable = false, length = 64)
    private String name;

    @Column(length = 8)
    private String symbol; // e.g., '$', '€'
}