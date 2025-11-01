package com.tps.mapper;

import org.springframework.stereotype.Component;

import com.tps.dto.UserResponse;
import com.tps.model.User;

@Component
public class UserMapper {
	
	public UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .firstName(user.getFirstName())
                .middleName(user.getMiddleName())
                .lastName(user.getLastName())
                .gmail(user.getGmail())
                .contactNumber(user.getContactNumber())
                .address(user.getAddress())
                .city(user.getCity())
                .zipCode(user.getZipCode())
                .countryName(user.getCountry() != null ? user.getCountry().getName() : null) 
                .stateName(user.getState() != null ? user.getState().getName() : null) 
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .build();
    }

}
