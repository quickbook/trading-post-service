package com.tps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.RiskProfile;

public interface RiskProfileRepository extends JpaRepository<RiskProfile, Long> {
}