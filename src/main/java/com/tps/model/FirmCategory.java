package com.tps.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "firm_categories")
@Data
public class FirmCategory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "category_key", nullable = false, unique = true)
    private String key;     // e.g., "ALL", "TOP_RATED"
    
    private String label;   // e.g., "All Firms", "Top Rated"
    private Integer sortOrder;
    private Boolean active = true;
}