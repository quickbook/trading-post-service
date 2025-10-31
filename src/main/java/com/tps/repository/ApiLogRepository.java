package com.tps.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tps.model.ApiLog;

@Repository
public interface ApiLogRepository extends JpaRepository<ApiLog, Long> { }