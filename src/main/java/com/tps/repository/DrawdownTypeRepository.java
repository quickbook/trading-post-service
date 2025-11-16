package com.tps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.DmnDrawdownType;

public interface DrawdownTypeRepository extends JpaRepository<DmnDrawdownType, Long> {
}