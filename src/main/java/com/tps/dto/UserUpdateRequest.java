package com.tps.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserUpdateRequest {
    
    @Size(max = 50, message = "First name must be under 50 characters")
    private String firstName;
    
    @Size(max = 50, message = "Middle name must be under 50 characters")
    private String middleName; 
    
    @Size(max = 50, message = "Last name must be under 50 characters")
    private String lastName;
    
    @Size(max = 15, message = "Contact number must be under 15 digits")
    private String contactNumber; 
    
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must be under 100 characters")
    private String gmail;
    
    @Size(min = 8, max = 255, message = "Password must be at least 8 characters long")
    private String password; 

    @Size(max = 255, message = "Address must be under 255 characters")
    private String address;
    
    @Size(max = 100, message = "City must be under 100 characters")
    private String city;
    
    @Size(max = 20, message = "ZIP code must be under 20 characters")
    private String zipCode;
    
    @Size(max = 10, message = "Country code must be under 10 characters (e.g., 'USA')")
    private String countryCode;
    
    @Size(max = 100, message = "State name must be under 50 characters")
    private String stateName; 
}