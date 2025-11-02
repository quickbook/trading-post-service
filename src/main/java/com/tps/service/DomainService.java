package com.tps.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tps.dto.DropdownOptionDto;
import com.tps.repository.CountryRepository;
import com.tps.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DomainService {

    private final CountryRepository countryRepository;
    private final RoleRepository roleRepository;

    
    public List<DropdownOptionDto> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(country -> new DropdownOptionDto(country.getCode(), country.getName()))
                .collect(Collectors.toList());
    }

    
    public List<DropdownOptionDto> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(role -> new DropdownOptionDto(role.getId().toString(), role.getName()))
                .collect(Collectors.toList());
    }
}