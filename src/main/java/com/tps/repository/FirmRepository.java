package com.tps.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tps.model.FirmCard;
import com.tps.model.FirmStatus; // Added import


@Repository
public interface FirmRepository extends JpaRepository<FirmCard, Long>, JpaSpecificationExecutor<FirmCard> {
	
	@Query("SELECT fc FROM FirmCard fc " +
	           "LEFT JOIN FETCH fc.assets " +
	           "LEFT JOIN FETCH fc.platforms " +
	           "WHERE fc.id = :id") 
	    Optional<FirmCard> findByIdWithDetails(Long id);
	
	boolean existsByName(String name);

	Optional<FirmCard> findByNameAndIdNot(String name, Long id);

    List<FirmCard> findByFirmStatus(FirmStatus firmStatus);
}