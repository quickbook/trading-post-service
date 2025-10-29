package com.tps.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tps.model.PhaseType;

public interface PhaseTypeRepository extends JpaRepository<PhaseType, Long> {
    List<PhaseType> findByActiveTrueOrderByIdAsc();
}
