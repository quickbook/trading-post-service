package com.tps.controller;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tps.dto.ApiResponse;
import com.tps.dto.LoginRequest;
import com.tps.dto.LoginResponse;
import com.tps.dto.RegisterRequest;
import com.tps.model.User;
import com.tps.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/firms/users")
@RequiredArgsConstructor
public class UserController {
	
	private final UserService userService;
	
	
	@PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@RequestBody LoginRequest loginRequest,HttpServletRequest request) {
        
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
	public ResponseEntity<ApiResponse<User>> registerUser(@RequestBody RegisterRequest registerRequest,HttpServletRequest request) {
	    
	    User savedUser = userService.userRegister(registerRequest);

	    ApiResponse<User> response = ApiResponse.<User>builder()
	            .success(true)
	            .message("User registered successfully") 
	            .data(savedUser) 
	            .status(HttpStatus.CREATED) 
	            .path(request.getRequestURI())
	            .timestamp(System.currentTimeMillis())
	            .build();

	   return  ResponseEntity.ok(response);
	}
	
}
