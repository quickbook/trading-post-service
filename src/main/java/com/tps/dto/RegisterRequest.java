package com.tps.dto;

import lombok.Data;

@Data
public class RegisterRequest {
	
	private String username;
	
	private String firstname;
	
	private String middlename;
	
	private String lastname;
	
	private String contactId;
	
	private String gmail;
	
	private String address;
	
	private String password;
}
