package com.tps.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
	
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;
    
    @NotBlank(message = "First name is required")
    private String firstName;
    
    private String middleName; // This one is optional
    
    @NotBlank(message = "Last name is required")
    private String lastName;
    
    @NotBlank(message = "Contact number is required")
    private String contactNumber; 
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String gmail;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

    @NotBlank(message = "Address is required")
    private String address;
    
    @NotBlank(message = "City is required")
    private String city;
    
    @NotBlank(message = "ZIP code is required") 
    private String zipCode;
    
    @NotBlank(message = "Country Name is required")
    private String countryName;
    
    @NotBlank(message = "Country Name is required")
    private String stateName; 
}