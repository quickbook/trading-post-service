package com.tps.repository;

import java.util.Optional; // ADDED
import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.DmnTradingPlatform;

public interface TradingPlatformRepository extends JpaRepository<DmnTradingPlatform, Long> {
    
    // NEW: Required method for mapping by code
    Optional<DmnTradingPlatform> findByCode(String code);
}