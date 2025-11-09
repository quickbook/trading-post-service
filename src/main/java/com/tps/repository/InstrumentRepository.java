package com.tps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.Instrument;

public interface InstrumentRepository extends JpaRepository<Instrument, Long> {
}