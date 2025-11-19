package com.tps.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tps.model.FirmReview;

public interface FirmReviewRepository extends JpaRepository<FirmReview,Long>{
	
	/**
     * Finds all active reviews for a specific firm.
     */
	List<FirmReview> findByFirmIdAndIsDeletedFalseOrderByCreatedAtDesc(Long firmId);
    /**
     * Finds all active reviews (not deleted).
     */
	List<FirmReview> findByIsDeletedFalseOrderByCreatedAtDesc();
}
