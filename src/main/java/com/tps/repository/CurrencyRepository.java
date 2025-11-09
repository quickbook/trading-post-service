package com.tps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, String> { 
}