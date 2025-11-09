package com.tps.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.ChallengeCardView;

public interface ChallengeCardViewRepository extends JpaRepository<ChallengeCardView, Long> {

    /**
     * Finds all challenge cards (active plans) associated with a specific firm ID.
     */
    List<ChallengeCardView> findByFirmId(Long firmId);
}