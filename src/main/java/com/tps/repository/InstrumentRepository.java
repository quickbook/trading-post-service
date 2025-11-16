package com.tps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.DmnInstrument;

public interface InstrumentRepository extends JpaRepository<DmnInstrument, Long> {
}