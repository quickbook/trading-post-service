package com.tps.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    
    
    
    
    private String firstName;
    
    private String middleName; 
    
    private String lastName;
    
    private String contactNumber; 
    
    @Email(message = "Invalid email format")
    private String gmail;
    
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password; 

    private String address;
    
    private String city;
    
    private String zipCode;
    
    private String countryCode;
    
    private String stateName; 
}