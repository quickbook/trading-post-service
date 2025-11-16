package com.tps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.DmnCountry;

public interface CountryRepository extends JpaRepository<DmnCountry,Long> {
	
	
	Optional<DmnCountry> findByCode(String countryCode); 
}
