package com.tps.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

 

import com.tps.model.FirmCategory;

public interface FirmCategoryRepository extends JpaRepository<FirmCategory, Long> {
    List<FirmCategory> findByActiveTrueOrderBySortOrderAsc();
}