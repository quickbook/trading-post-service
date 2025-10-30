package com.tps.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "state_details") 
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    
    @Column(name = "state_code", unique = true, nullable = false, length = 10)
    private String code;

    @Column(name = "state_name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id")
    private Country country;
}