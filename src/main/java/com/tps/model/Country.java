package com.tps.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "country_details") 
public class Country {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    
    @Column(name = "country_code", unique = true, nullable = false, length = 10)
    private String code;

    @Column(name = "country_name")
    private String name;
    
   
}