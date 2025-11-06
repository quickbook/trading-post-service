package com.tps.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tps.model.FirmCard;


@Repository
public interface FirmRepository extends JpaRepository<FirmCard, Long>, JpaSpecificationExecutor<FirmCard> {
	
	@Query("SELECT fc FROM FirmCard fc " +
	           "LEFT JOIN FETCH fc.assets " +
	           "LEFT JOIN FETCH fc.platforms " +
	           "LEFT JOIN FETCH fc.challenges c " +
	           "LEFT JOIN FETCH c.phases " +
	           "WHERE fc.id = :id")
	    Optional<FirmCard> findByIdWithDetails(Long id);
	
	

	boolean existsByTitle(String title);



	Optional<FirmCard> findByTitleAndIdNot(String title, Long id);



	Optional<FirmCard> findByCodeAndIdNot(String code, Long id);

}