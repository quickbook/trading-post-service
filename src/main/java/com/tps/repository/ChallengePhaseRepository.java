package com.tps.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tps.model.DmnChallengePhase;

public interface ChallengePhaseRepository extends JpaRepository<DmnChallengePhase, Long> {
    /**
     * Finds all challenge phases ordered by their code for consistent display.
     */
    List<DmnChallengePhase> findAllByOrderByCodeAsc();

}