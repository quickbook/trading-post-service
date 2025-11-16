package com.tps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.DmnPayoutFrequency;

public interface PayoutFrequencyRepository extends JpaRepository<DmnPayoutFrequency, Long> {
}