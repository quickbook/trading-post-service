package com.tps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.DmnTier;

public interface TierRepository extends JpaRepository<DmnTier, Long> {
}