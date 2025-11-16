package com.tps.dto.response;

public record LoginResponse(
    UserResponse user, 
    String accessToken, 
    String refreshToken, 
    long expiresIn
) {
}