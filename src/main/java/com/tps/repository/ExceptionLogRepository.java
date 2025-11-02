package com.tps.repository;
 

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.tps.model.ExceptionLog;

public interface ExceptionLogRepository extends JpaRepository<ExceptionLog, Long> {

	@Modifying
	@Transactional
	@Query("DELETE FROM ExceptionLog e WHERE e.timestamp < :cutoff")
	int deleteOlderThan(@Param("cutoff") Instant cutoff);
}
