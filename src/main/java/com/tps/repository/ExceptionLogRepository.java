package com.tps.repository;
 

import org.springframework.data.jpa.repository.JpaRepository;

import com.tps.model.ExceptionLog;

public interface ExceptionLogRepository extends JpaRepository<ExceptionLog, Long> { }
