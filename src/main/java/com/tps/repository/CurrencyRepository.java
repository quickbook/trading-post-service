package com.tps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.DmnCurrency;

public interface CurrencyRepository extends JpaRepository<DmnCurrency, String> { 
}