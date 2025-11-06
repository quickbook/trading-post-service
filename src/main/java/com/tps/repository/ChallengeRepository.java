package com.tps.repository;

import com.tps.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    List<Challenge> findByFirmCardId(Long firmId);

    Optional<Challenge> findByIdAndFirmCardId(Long id, Long firmId);

    
    @Query("SELECT c FROM Challenge c LEFT JOIN FETCH c.phases WHERE c.id = :id")
    Optional<Challenge> findByIdWithPhases(Long id);
}