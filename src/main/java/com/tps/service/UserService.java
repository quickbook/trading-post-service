package com.tps.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tps.dto.LoginRequest;
import com.tps.dto.LoginResponse;
import com.tps.dto.RegisterRequest;
import com.tps.model.Role;
import com.tps.model.User;
import com.tps.repository.RoleRepository;
import com.tps.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository userRepository;
	
	private final RoleRepository roleRepository;
	
	private final PasswordEncoder passwordEncoder;	
	
public LoginResponse checkLoginDetails(LoginRequest loginRequest) {
        
        
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        Role userRole = user.getRole();

        LoginResponse loginData = new LoginResponse();
        loginData.setUsername(user.getUsername());
        loginData.setRoleid(userRole.getId());
        loginData.setRolename(userRole.getName());
        
        return loginData;
    }

public User userRegister(RegisterRequest registerRequest) {
    if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
        throw new RuntimeException("Error: Username is already taken!");
    }

    if (userRepository.existsByGmail(registerRequest.getGmail())) { 
        throw new RuntimeException("Error: Email is already in use!");
    }

    User user = new User();
    
    user.setUsername(registerRequest.getUsername());
    user.setFirstname(registerRequest.getFirstname());
    user.setMiddlename(registerRequest.getMiddlename());
    user.setLastname(registerRequest.getLastname());
    user.setContactId(registerRequest.getContactId());
    user.setGmail(registerRequest.getGmail());
    user.setAddress(registerRequest.getAddress());

    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    // Defualt Role is "USER"
    Role userRole = roleRepository.findByName("USER")
            .orElseThrow(() -> new RuntimeException("Error: Default role not found."));
    user.setRole(userRole);

    // 7. Save the user to the database
    User savedUser = userRepository.save(user);

    return savedUser;
}

}
