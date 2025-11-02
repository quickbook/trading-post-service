package com.tps.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tps.dto.CountryDto;
import com.tps.dto.RoleDto;
import com.tps.repository.CountryRepository;
import com.tps.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DomainService {

    private final CountryRepository countryRepository;
    private final RoleRepository roleRepository;

    
    public List<CountryDto> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(country -> new CountryDto(country.getCode(), country.getName()))
                .collect(Collectors.toList());
    }

    
    public List<RoleDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new RoleDto(role.getId(), role.getName()))
                .collect(Collectors.toList());
    }
}