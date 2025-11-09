package com.tps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.PayoutFrequency;

public interface PayoutFrequencyRepository extends JpaRepository<PayoutFrequency, Long> {
}