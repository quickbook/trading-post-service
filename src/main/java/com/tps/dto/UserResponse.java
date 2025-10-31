package com.tps.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    
    private Long id;
    private String userName;
    private String firstName;
    private String middleName;
    private String lastName;
    private String gmail;
    private String contactNumber;
    private String address;
    private String city;
    private String zipCode;   
    private String countryCode; 
    private String countryName;
    private String stateCode;   
    private String stateName; 
    private String roleName;    
    
}