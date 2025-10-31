package com.tps.dto;

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
    private String countryCode; // e.g. "IN"
    private String stateCode;   // e.g. "MH"
    private String roleName;    // e.g. "USER"
    
}

