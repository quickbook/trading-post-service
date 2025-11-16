package com.tps.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterResponse {
    
    private Long id;
    private String userName;
    private String firstName;
    private String lastName;
    private String gmail;
    private String address;
    private String zipCode; 
    private String countryName; 
    private String stateName; 
    private String roleName;    
    
}