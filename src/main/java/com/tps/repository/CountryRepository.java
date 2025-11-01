package com.tps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tps.model.Country;

public interface CountryRepository extends JpaRepository<Country,Long> {
	
	@Query("SELECT c FROM Country c WHERE LOWER(c.name) = LOWER(:input) OR LOWER(c.code) = LOWER(:input)")
	Optional<Country> findByNameOrCodeIgnoreCase(@Param("input") String input); 
}
