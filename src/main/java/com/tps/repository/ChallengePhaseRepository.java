package com.tps.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tps.model.ChallengePhase;

public interface ChallengePhaseRepository extends JpaRepository<ChallengePhase, Long> {
    /**
     * Finds all challenge phases ordered by their code for consistent display.
     */
    List<ChallengePhase> findAllByOrderByCodeAsc();

}