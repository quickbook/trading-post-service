package com.tps.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tps.enums.FirmStatus;
import com.tps.model.FirmCard;


@Repository
public interface FirmRepository extends JpaRepository<FirmCard, Long>, JpaSpecificationExecutor<FirmCard> {
	
	@Query("""
		    SELECT DISTINCT f FROM FirmCard f
		    LEFT JOIN FETCH f.platforms p
		    LEFT JOIN FETCH f.assets a
		    LEFT JOIN FETCH f.leverages l
		    WHERE f.id = :id
		""")
	    Optional<FirmCard> findByIdWithDetails(Long id);
	
	boolean existsByName(String name);

	Optional<FirmCard> findByNameAndIdNot(String name, Long id);

	List<FirmCard> findByFirmStatusOrderByNameAsc(FirmStatus firmStatus);
	List<FirmCard> findAllByOrderByNameAsc();
 
}