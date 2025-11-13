package com.tps.service;

import java.time.Duration;
import java.time.Instant;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.tps.dto.LoginRequest;
import com.tps.dto.LoginResponseDto;
import com.tps.dto.RegisterRequest;
import com.tps.dto.RegisterResponse;
import com.tps.dto.TokenCacheEntry;
import com.tps.dto.UserResponse;
import com.tps.dto.UserUpdateRequest;
import com.tps.exceptions.DuplicateResourceException;
import com.tps.exceptions.InvalidCredentialsException;
import com.tps.exceptions.ResourceNotFoundException;
import com.tps.mapper.UserMapper;
import com.tps.model.Country;
import com.tps.model.Role;
import com.tps.model.User;
import com.tps.repository.CountryRepository;
import com.tps.repository.RoleRepository;
import com.tps.repository.UserRepository;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	private final RoleRepository roleRepository;
	private final CountryRepository countryRepository;
	private final TokenService tokenService;

	private final UserMapper userMapper;

	private final PasswordEncoder passwordEncoder;

	public LoginResponseDto checkLoginDetails(LoginRequest loginRequest, String clientIp) {

		User user = userRepository.findByUserNameOrGmail(loginRequest.getUsername())
				.orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

		if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid username or password");
		}
		// 1. Get Role Name from the User Entity
        String roleName = user.getRole() != null ? user.getRole().getName() : "USER";
        
        // 2. Issue Token with Role
        TokenCacheEntry entry = tokenService.issueTokenWithRole(clientIp, roleName);
        long expiresIn = Duration.between(Instant.now(), entry.accessExpiry()).toSeconds();
        UserResponse userData = userMapper.mapToUserResponse(user);
        return new LoginResponseDto(
                userData, 
                entry.accessToken(), 
                entry.refreshToken(), 
                expiresIn
            );
	}

	public RegisterResponse userRegister(@Valid RegisterRequest registerRequest) {

		if (userRepository.findByUserName(registerRequest.getUserName()).isPresent()) {
			throw new DuplicateResourceException("Username  is already taken!");
		}

		if (userRepository.existsByGmail(registerRequest.getGmail())) {
			throw new DuplicateResourceException("Error: Email is already in use!");
		}

		Country country = countryRepository.findByCode(registerRequest.getCountryCode()).orElseThrow(
				() -> new ResourceNotFoundException("Country not found for code: " + registerRequest.getCountryCode()));

	 
		User user = new User();

		user.setUserName(registerRequest.getUserName());

		user.setFirstName(registerRequest.getFirstName().toUpperCase());
		user.setMiddleName(
				registerRequest.getMiddleName() != null ? registerRequest.getMiddleName().toUpperCase() : null);
		user.setLastName(registerRequest.getLastName().toUpperCase());

		user.setContactNumber(registerRequest.getContactNumber());
		user.setGmail(registerRequest.getGmail());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

		user.setAddress(registerRequest.getAddress());
		user.setCity(registerRequest.getCity());
		user.setZipCode(registerRequest.getZipCode());
		user.setCountry(country);
		user.setStateName(registerRequest.getStateName());

		// Defualt Role is USER
		Role userRole = roleRepository.findByName("USER")
				.orElseThrow(() -> new RuntimeException("Error: Default role not found."));
		user.setRole(userRole);

		User savedUser = userRepository.save(user);

		return RegisterResponse.builder().id(savedUser.getId()).userName(savedUser.getUserName())
				.firstName(savedUser.getFirstName()).lastName(savedUser.getLastName()).gmail(savedUser.getGmail())
				.address(savedUser.getAddress()).zipCode(savedUser.getZipCode())
				.countryName(savedUser.getCountry().getName())
				.stateName(savedUser.getStateName())
				.roleName(savedUser.getRole().getName()).build();
	}
	
	@Transactional
    public UserResponse updateUser(String userName, @Valid UserUpdateRequest dto) {
        
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + userName));

        if (StringUtils.hasText(dto.getFirstName())) {
            user.setFirstName(dto.getFirstName().toUpperCase());
        }
        if (StringUtils.hasText(dto.getLastName())) {
            user.setLastName(dto.getLastName().toUpperCase());
        }
        if (dto.getMiddleName() != null) {
            user.setMiddleName(dto.getMiddleName().isEmpty() ? null : dto.getMiddleName().toUpperCase());
        }
        if (StringUtils.hasText(dto.getContactNumber())) {
            user.setContactNumber(dto.getContactNumber());
        }
        if (StringUtils.hasText(dto.getAddress())) {
            user.setAddress(dto.getAddress());
        }
        if (StringUtils.hasText(dto.getCity())) {
            user.setCity(dto.getCity());
        }
        if (StringUtils.hasText(dto.getZipCode())) {
            user.setZipCode(dto.getZipCode());
        }
        if (StringUtils.hasText(dto.getStateName())) {
            user.setStateName(dto.getStateName());
        }

        if (StringUtils.hasText(dto.getPassword())) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        if (StringUtils.hasText(dto.getCountryCode()) && !dto.getCountryCode().equals(user.getCountry().getCode())) {
            Country newCountry = countryRepository.findByCode(dto.getCountryCode())
                    .orElseThrow(() -> new ResourceNotFoundException("Country not found for code: " + dto.getCountryCode()));
            user.setCountry(newCountry);
        }

        if (StringUtils.hasText(dto.getGmail()) && !dto.getGmail().equalsIgnoreCase(user.getGmail())) {
            if (userRepository.existsByGmail(dto.getGmail())) {
                throw new DuplicateResourceException("Error: Email is already in use!");
            }
            user.setGmail(dto.getGmail());
        }
        
       User updatedUser = userRepository.save(user);
        return userMapper.mapToUserResponse(updatedUser);
    }

}
