package com.tps.dto.request;

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
    @Size(max = 50, message = "First name must be under 50 characters")
    private String firstName;
    
    @Size(max = 50, message = "Middle name must be under 50 characters")
    private String middleName; 
    
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must be under 50 characters")
    private String lastName;
    
    @NotBlank(message = "Contact number is required")
    @Size(max = 15, message = "Contact number must be under 15 digits")
    private String contactNumber; 
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must be under 100 characters")
    private String gmail;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be at least 8 characters long") // Assuming hashed passwords are long
    private String password;

    @NotBlank(message = "Address is required")
    @Size(max = 255, message = "Address must be under 255 characters")
    private String address;
    
    @NotBlank(message = "City is required")
    @Size(max = 100, message = "City must be under 100 characters")
    private String city;
    
    @NotBlank(message = "ZIP code is required") 
    @Size(max = 20, message = "ZIP code must be under 20 characters")
    private String zipCode;
    
    @NotBlank(message = "Country Code is required")
    @Size(max = 10, message = "Country code must be under 10 characters (e.g., 'USA')")
    private String countryCode;
    
    @Size(max = 100, message = "State name must be under 50 characters")
    private String stateName; 
}