package com.tps.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.ViewFirmChallenges;

public interface ChallengeCardViewRepository extends JpaRepository<ViewFirmChallenges, Long> {

    /**
     * Finds all challenge cards (active plans) associated with a specific firm ID.
     */
    List<ViewFirmChallenges> findByFirmId(Long firmId);
}