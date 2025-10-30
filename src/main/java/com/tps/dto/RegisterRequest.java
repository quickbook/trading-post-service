package com.tps.dto;

import lombok.Data;

@Data
public class RegisterRequest {
	
    private String userName;
    
    private String firstName;
    private String middleName;
    private String lastName;
    
    private String contactNumber; 
    private String gmail;
    private String password;

    private String address;
    private String city;
    private String pinCode;
    private String countryCode;
    private String stateCode; 
}