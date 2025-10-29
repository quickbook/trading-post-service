package com.tps.repository;

import com.tps.model.FirmCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FirmRepository extends JpaRepository<FirmCard, Long> {
}