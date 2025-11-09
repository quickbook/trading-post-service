package com.tps.repository;

import java.util.Optional; // ADDED
import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.TradingPlatform;

public interface TradingPlatformRepository extends JpaRepository<TradingPlatform, Long> {
    
    // NEW: Required method for mapping by code
    Optional<TradingPlatform> findByCode(String code);
}