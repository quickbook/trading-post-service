package com.tps.controller;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tps.dto.ApiResponse;
import com.tps.dto.LoginRequest;
import com.tps.dto.LoginResponseDto;
import com.tps.dto.RegisterRequest;
import com.tps.dto.RegisterResponse;
import com.tps.dto.UserResponse;
import com.tps.dto.UserUpdateRequest;
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
    public ResponseEntity<ApiResponse<LoginResponseDto>> loginUser(@Valid @RequestBody LoginRequest loginRequest,HttpServletRequest request) {
		
		String clientIp = clientIp(request);
		
		LoginResponseDto loginData = userService.checkLoginDetails(loginRequest, clientIp);
		
        ApiResponse<LoginResponseDto> response = ApiResponse.<LoginResponseDto>builder()
                .success(true)
                .message("Login successful")
                .data(loginData)
                .status(HttpStatus.OK)
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build();

       
        return ResponseEntity.ok(response);
    }
	// Helper Method, to extract the IP addresses from user API Request
	private String clientIp(HttpServletRequest req) {
		String xff = req.getHeader("X-Forwarded-For");
		if (xff != null && !xff.isBlank())
			return xff.split(",")[0].trim();
		return req.getRemoteAddr();
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
	
	@PutMapping("/{userName}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String userName,
            @Valid @RequestBody UserUpdateRequest updateRequest,
            HttpServletRequest request) {
        
        UserResponse updatedUser = userService.updateUser(userName, updateRequest);

        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .success(true)
                .message("User updated successfully")
                .data(updatedUser)
                .status(HttpStatus.OK)
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build();

        return ResponseEntity.ok(response);
    }
}
