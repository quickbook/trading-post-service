package com.tps.service; 

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tps.repository.ApiLogRepository;

import jakarta.transaction.Transactional;

@Service
public class DataCleanupService {
    private final ApiLogRepository repo;

    public DataCleanupService(ApiLogRepository repo) {
        this.repo = repo;
    }

    @Transactional
    @Scheduled(cron = "0 0 2 1,16 * ?")
    public void cleanOldLogs() {
        Instant cutoff = Instant.now().minus(90, ChronoUnit.DAYS);
        repo.deleteAll(repo.findAll()
                .stream()
                .filter(l -> l.getTimestamp().isBefore(cutoff))
                .toList());
    }
}
