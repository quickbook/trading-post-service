package com.tps.controller;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tps.dto.ApiResponse;
import com.tps.dto.LoginRequest;
import com.tps.dto.LoginResponse;
import com.tps.dto.RegisterRequest;
import com.tps.dto.RegisterResponse;
import com.tps.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tradingpost/api/v1/users")
@RequiredArgsConstructor
@Validated 
public class UserController {
	
	private final UserService userService;
	
	
	@PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@Valid @RequestBody LoginRequest loginRequest,HttpServletRequest request) {
        
		LoginResponse loginData = userService.checkLoginDetails(loginRequest);

        ApiResponse<LoginResponse> response = ApiResponse.<LoginResponse>builder()
                .success(true)
                .message("Login successful")
                .data(loginData)
                .status(HttpStatus.OK)
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build();

       
        return ResponseEntity.ok(response);
    }
	
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<RegisterResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest,HttpServletRequest request) {
	    
	    RegisterResponse savedUser = userService.userRegister(registerRequest);

	    ApiResponse<RegisterResponse> response = ApiResponse.<RegisterResponse>builder()
	            .success(true)
	            .message("User registered successfully") 
	            .data(savedUser) 
	            .status(HttpStatus.CREATED) 
	            .path(request.getRequestURI())
	            .timestamp(System.currentTimeMillis())
	            .build();

	    return ResponseEntity.status(HttpStatus.CREATED).body(response);	
	  }
	
}
