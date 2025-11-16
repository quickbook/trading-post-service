package com.tps.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "user_details")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String userName; 

    @Column(nullable = false)
    private String password; 

    @Column(name = "first_name",nullable=false)
    private String firstName; 

    @Column(name = "middle_name",nullable=true)
    private String middleName; 
    
    @Column(name = "last_name",nullable=false)
    private String lastName; 
    
    @Column(name = "contact_number",nullable=false)
    private String contactNumber; 

    @Column(unique = true,nullable=false)
    private String gmail;

    @Column(name="address",nullable=false)
    private String address;
    
    @Column(name = "city",nullable=false)
    private String city;

    @Column(name = "pin_code",nullable=false)
    private String zipCode;

    
    @ManyToOne
    @JoinColumn(name = "country_id",nullable=false)
    private DmnCountry country;

 
    private String stateName;

    @ManyToOne
    @JoinColumn(name = "role_id",nullable=false)
    private Role role;
}
