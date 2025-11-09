package com.tps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.Tier;

public interface TierRepository extends JpaRepository<Tier, Long> {
}