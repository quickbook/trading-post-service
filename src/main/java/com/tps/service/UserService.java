package com.tps.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tps.dto.LoginRequest;
import com.tps.dto.LoginResponse;
import com.tps.dto.RegisterRequest;
import com.tps.dto.RegisterResponse;
import com.tps.exceptions.DuplicateResourceException;
import com.tps.exceptions.InvalidCredentialsException;
import com.tps.exceptions.ResourceNotFoundException;
import com.tps.model.Country;
import com.tps.model.Role;
import com.tps.model.State;
import com.tps.model.User;
import com.tps.repository.CountryRepository;
import com.tps.repository.RoleRepository;
import com.tps.repository.StateRepository;
import com.tps.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository userRepository;
	
	private final RoleRepository roleRepository;
	private final CountryRepository countryRepository;
	private final StateRepository stateRepository;
	
	private final PasswordEncoder passwordEncoder;	
	
public LoginResponse checkLoginDetails(LoginRequest loginRequest) {
        
        
        User user = userRepository.findByUserName(loginRequest.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        Role userRole = user.getRole();

        LoginResponse loginData = new LoginResponse();
        loginData.setUsername(user.getUserName());
        loginData.setRoleid(userRole.getId());
        loginData.setRolename(userRole.getName());
        
        return loginData;
    }

	public RegisterResponse userRegister(@Valid RegisterRequest registerRequest) {
		if (userRepository.findByUserName(registerRequest.getUserName()).isPresent()) {
	        throw new DuplicateResourceException("Username  is already taken!");
	    }
	
	    if (userRepository.existsByGmail(registerRequest.getGmail())) { 
	        throw new DuplicateResourceException("Error: Email is already in use!");
	    }
	    
	 
	    Country country = countryRepository.findByCode(registerRequest.getCountryCode())
	            .orElseThrow(() -> new ResourceNotFoundException("Country not found for code: " + registerRequest.getCountryCode()));
	
	    
	    State state = null; 
	    if (registerRequest.getStateCode() != null && !registerRequest.getStateCode().isBlank()) {
	        state = stateRepository.findByCode(registerRequest.getStateCode())
	                .orElseThrow(() -> new ResourceNotFoundException("State not found for code: " + registerRequest.getStateCode()));
	    }
	
	    User user = new User();
	    
	    user.setUserName(registerRequest.getUserName());
	    
	    user.setFirstName(registerRequest.getFirstName().toUpperCase());
	    user.setMiddleName(registerRequest.getMiddleName() != null ? registerRequest.getMiddleName().toUpperCase() : null);
	    user.setLastName(registerRequest.getLastName().toUpperCase());
	    
	    user.setContactNumber(registerRequest.getContactNumber());
	    user.setGmail(registerRequest.getGmail());
	    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
	    
	    user.setAddress(registerRequest.getAddress());
	    user.setCity(registerRequest.getCity());
	    user.setPinCode(registerRequest.getPinCode());
	    user.setCountry(country); 
	    user.setState(state);
	    
	    // Defualt Role is USER
	    Role userRole = roleRepository.findByName("USER")
	            .orElseThrow(() -> new RuntimeException("Error: Default role not found."));
	    user.setRole(userRole);
	
	    
	    User savedUser = userRepository.save(user);
	
	    return RegisterResponse.builder()
	            .id(savedUser.getId())
	            .userName(savedUser.getUserName())
	            .firstName(savedUser.getFirstName())
	            .lastName(savedUser.getLastName())
	            .gmail(savedUser.getGmail())
	            .address(savedUser.getAddress())
	            .countryCode(savedUser.getCountry().getCode())
	            .stateCode(savedUser.getState() != null ? savedUser.getState().getCode() : null)
	            .roleName(savedUser.getRole().getName())
	            .build();
	}

}
