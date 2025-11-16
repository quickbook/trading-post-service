package com.tps.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tps.dto.ErrorDetails;
import com.tps.dto.response.ApiResponse;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    // ObjectMapper is required to serialize the ApiResponse to JSON
    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, 
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // 1. Construct detailed error message
        ErrorDetails details = new ErrorDetails(
            "ACCESS_DENIED", 
            "Your user role (USER) does not grant access to perform this operation.", 
            null, 
            accessDeniedException.getClass().getSimpleName()
        );

        // 2. Wrap into the standard ApiResponse format
        ApiResponse<Object> apiResponse = ApiResponse.builder()
            .success(false)
            .message("Authorization Failed")
            .data(null)
            .errorDetails(details)
            .status(HttpStatus.FORBIDDEN)
            .path(request.getRequestURI())
            .timestamp(System.currentTimeMillis())
            .build();

        // 3. Write customized JSON response
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}