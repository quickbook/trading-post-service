package com.tps.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tps.dto.ApiResponse;
import com.tps.dto.DropdownOptionDto;
import com.tps.service.DomainService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tradingpost/api/v1/domaindata")
@RequiredArgsConstructor
public class DomainDataController {

    private final DomainService domainService;

    @GetMapping("/countries")
    public ResponseEntity<ApiResponse<List<DropdownOptionDto>>> getAllCountries(HttpServletRequest request) {
        
        List<DropdownOptionDto> countries = domainService.getAllCountries();
        
        return ResponseEntity.ok(
            ApiResponse.<List<DropdownOptionDto>>builder()
                .success(true)
                .message("Countries fetched successfully")
                .data(countries)
                .status(HttpStatus.OK)
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<DropdownOptionDto>>> getAllRoles(HttpServletRequest request) {
        
        List<DropdownOptionDto> roles = domainService.getAllRoles();
        
        return ResponseEntity.ok(
            ApiResponse.<List<DropdownOptionDto>>builder()
                .success(true)
                .message("Roles fetched successfully")
                .data(roles)
                .status(HttpStatus.OK)
                .path(request.getRequestURI())
                .timestamp(System.currentTimeMillis())
                .build()
        );
    }
}