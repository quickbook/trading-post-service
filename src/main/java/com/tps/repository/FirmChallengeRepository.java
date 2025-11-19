package com.tps.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.tps.model.FirmChallenge;

public interface FirmChallengeRepository extends JpaRepository<FirmChallenge, Long> {

	List<FirmChallenge> findByFirmCardIdOrderByProfitTargetPctDescAccountSizeUsdDescPriceAmountDesc(Long firmId);}