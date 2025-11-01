package com.tps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.Country;

public interface CountryRepository extends JpaRepository<Country,Long> {
	
	
	Optional<Country> findByCode(String countryCode); 
}
