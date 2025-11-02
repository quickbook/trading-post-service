package com.tps.service; 

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.tps.repository.ApiLogRepository;
import com.tps.repository.ExceptionLogRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DataCleanupService {
	
    private final ApiLogRepository apiLogRepository;
    private final ExceptionLogRepository exceptionLogRepository;

  

    @Transactional
    @Scheduled(cron = "0 0 2 1,16 * ?")
    public void cleanOldLogs() {
        Instant cutoff = Instant.now().minus(90, ChronoUnit.DAYS);
        apiLogRepository.deleteAll(apiLogRepository.findAll()
                .stream()
                .filter(l -> l.getTimestamp().isBefore(cutoff))
                .toList());
    }
    
    @Transactional
    @Scheduled(cron = "0 0 3 1,16 * ?")
    public void cleanOldExceptionLogs() {
        Instant cutoff = Instant.now().minus(15, ChronoUnit.DAYS);
        int deleted = exceptionLogRepository.deleteOlderThan(cutoff);
        
        
    }
}
