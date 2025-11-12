package com.tps.dto;


public record LoginResponseDto(
    UserResponse user, 
    String accessToken, 
    String refreshToken, 
    long expiresIn
) {
}