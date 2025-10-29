package com.tps.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.tps.dto.DropdownOptionDto;

@Service
public class CommonDataService {
	
	public List<DropdownOptionDto> getMinAccountSizeOptions() {
        return List.of(
                new DropdownOptionDto("ANY", "Any Size"),
                new DropdownOptionDto("5000", "5,000"),
                new DropdownOptionDto("10000", "10,000"),
                new DropdownOptionDto("25000", "25,000"),
                new DropdownOptionDto("50000", "50,000"),
                new DropdownOptionDto("100000", "100,000")
        );
    }

    /**
     * Initializes static list for Sort By filter dropdown.
     */
    public List<DropdownOptionDto> getSortOptions() {
        return List.of(
                new DropdownOptionDto("ACCOUNT_SIZE", "Account Size"),
                new DropdownOptionDto("PRICE", "Price")
        );
    }

}
