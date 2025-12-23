package com.tps.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tps.dto.request.LoginRequest;
import com.tps.dto.request.RegisterRequest;
import com.tps.dto.request.UserRequest;
import com.tps.dto.response.ApiResponse;
import com.tps.dto.response.LoginResponse;
import com.tps.dto.response.RegisterResponse;
import com.tps.dto.response.UserResponse;
import com.tps.enums.RoleEnum;
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
	
	@Value("${app.root.tokenId}")
	private String rootTokenId;
	
	
	@PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@Valid @RequestBody LoginRequest loginRequest,HttpServletRequest request) {
		
		String clientIp = clientIp(request);
		
		LoginResponse loginData = userService.checkLoginDetails(loginRequest, clientIp);
		
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
	// Helper Method, to extract the IP addresses from user API Request
	private String clientIp(HttpServletRequest req) {
		String xff = req.getHeader("X-Forwarded-For");
		if (xff != null && !xff.isBlank())
			return xff.split(",")[0].trim();
		return req.getRemoteAddr();
	}
	
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<RegisterResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest,HttpServletRequest request) {
	    
	    RegisterResponse savedUser = userService.userRegister(registerRequest,RoleEnum.USER.name());

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
	
	/* Endpoint to update user details */
	@PutMapping("/{userName}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable String userName,
            @Valid @RequestBody UserRequest updateRequest,
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
	@PostMapping("/createAdminUser")
	public ResponseEntity<ApiResponse<RegisterResponse>> createuser(@Valid @RequestBody RegisterRequest registerRequest,HttpServletRequest request) {
	    
	    RegisterResponse savedUser = userService.userRegister(registerRequest,RoleEnum.ADMIN.name());

	    ApiResponse<RegisterResponse> response = ApiResponse.<RegisterResponse>builder()
	            .success(true)
	            .message("Admin User created successfully") 
	            .data(savedUser) 
	            .status(HttpStatus.CREATED) 
	            .path(request.getRequestURI())
	            .timestamp(System.currentTimeMillis())
	            .build();

	    return ResponseEntity.status(HttpStatus.CREATED).body(response);	
	  }
	
	@PostMapping("/createRootUser")
	public ResponseEntity<ApiResponse<RegisterResponse>> createRootUser(@Valid @RequestBody RegisterRequest registerRequest,HttpServletRequest request) {
	    
		 // Read tokenId from request header
	    String tokenId = request.getHeader("tokenId");

	    // Validate token
	    if (tokenId == null || tokenId.isEmpty() || !tokenId.equals(rootTokenId)) {
	        ApiResponse<RegisterResponse> errorResponse = ApiResponse.<RegisterResponse>builder()
	                .success(false)
	                .message("Invalid or missing tokenId")
	                .status(HttpStatus.UNAUTHORIZED)
	                .path(request.getRequestURI())
	                .timestamp(System.currentTimeMillis())
	                .build();

	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	    }
	    
	    RegisterResponse savedUser = userService.userRegister(registerRequest,RoleEnum.ROOT.name());

	    ApiResponse<RegisterResponse> response = ApiResponse.<RegisterResponse>builder()
	            .success(true)
	            .message("Root User created successfully") 
	            .data(savedUser) 
	            .status(HttpStatus.CREATED) 
	            .path(request.getRequestURI())
	            .timestamp(System.currentTimeMillis())
	            .build();

	    return ResponseEntity.status(HttpStatus.CREATED).body(response);	
	  }
	
	@GetMapping("/all")
	public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers(
	        Authentication authentication, 
	        HttpServletRequest request) {
	    
	    // Extract role from the token (provided by JwtAuthFilter)
	    // Note: Roles in Spring Security are prefixed with "ROLE_"
	    String roleName = authentication.getAuthorities().stream()
	            .map(GrantedAuthority::getAuthority)
	            .map(r -> r.replace("ROLE_", ""))
	            .findFirst()
	            .orElse("USER");

	    List<UserResponse> users = userService.getAllUsersByRole(roleName);

	    return ResponseEntity.ok(
	        ApiResponse.<List<UserResponse>>builder()
	            .success(true)
	            .message("User details fetched based on " + roleName + " privileges")
	            .data(users)
	            .status(HttpStatus.OK)
	            .path(request.getRequestURI())
	            .timestamp(System.currentTimeMillis())
	            .build()
	    );
	}
}
