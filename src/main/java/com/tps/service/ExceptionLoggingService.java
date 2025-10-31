package com.tps.service;
 

import java.time.Instant;

import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tps.config.CorrelationIdFilter;
import com.tps.model.ExceptionLog;
import com.tps.repository.ExceptionLogRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class ExceptionLoggingService {

    private final ExceptionLogRepository repo;

    public ExceptionLoggingService(ExceptionLogRepository repo) {
        this.repo = repo;
    }

    @Async
    public void save(HttpServletRequest req, Throwable ex, Integer status, String clientId) {
        String stack = stackTrace(ex, 8000); // truncate to 8k chars
        String ip = clientIp(req);
        String correlationId = MDC.get(CorrelationIdFilter.CORRELATION_ID);

        ExceptionLog log = ExceptionLog.builder()
            .timestamp(Instant.now())
            .correlationId(correlationId)
            .clientId(clientId)
            .ipAddress(ip)
            .httpMethod(req.getMethod())
            .path(req.getRequestURI())
            .statusCode(status)
            .message(safe(ex.getMessage(), 2000))
            .stackTrace(stack)
            .exceptionType(ex.getClass().getName())
            .build();

        repo.save(log);
    }

    private static String clientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        return req.getRemoteAddr();
    }

    private static String stackTrace(Throwable t, int maxLen) {
        StringBuilder sb = new StringBuilder();
        sb.append(t.toString()).append("\n");
        for (StackTraceElement el : t.getStackTrace()) {
            if (sb.length() > maxLen) break;
            sb.append("    at ").append(el.toString()).append("\n");
        }
        return sb.length() > maxLen ? sb.substring(0, maxLen) : sb.toString();
    }

    private static String safe(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }
}

