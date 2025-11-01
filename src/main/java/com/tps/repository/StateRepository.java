package com.tps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tps.model.State;

public interface StateRepository extends JpaRepository<State,Long>{
	
	@Query("SELECT s FROM State s WHERE LOWER(s.name) = LOWER(:input) OR LOWER(s.code) = LOWER(:input)")
	Optional<State> findByNameOrCodeIgnoreCase(@Param("input") String input);
}
