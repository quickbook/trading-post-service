package com.tps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.DrawdownType;

public interface DrawdownTypeRepository extends JpaRepository<DrawdownType, Long> {
}