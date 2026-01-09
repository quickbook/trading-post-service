package com.tps.model;

 
import java.time.Instant;
 

import org.springframework.data.annotation.CreatedDate;
 

 

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "contact_enquiry")
public class ContactEnquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String firm;
    private String whatsapp;
    private String services;
    

    @Column(length = 1000)
    private String aboutFirm;

    private Boolean consent;
    
    // --- AUDITING FIELDS ---
    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

   




    // getters & setters
}
