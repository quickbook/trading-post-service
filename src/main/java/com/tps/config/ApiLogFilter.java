package com.tps.config;
 

import java.io.IOException;
import java.time.Instant;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tps.model.ApiLog;
import com.tps.repository.ApiLogRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiLogFilter extends OncePerRequestFilter {

    private final ApiLogRepository repository;

    public ApiLogFilter(ApiLogRepository repository) {
        this.repository = repository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        long start = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;

            String clientId = request.getHeader("X-Client-Id");
            String ip = clientIp(request);

            ApiLog log = ApiLog.builder()
                    .clientId(clientId)
                    .ipAddress(ip)
                    .httpMethod(request.getMethod())
                    .endpoint(request.getRequestURI())
                    .statusCode(response.getStatus())
                    .durationMs(duration)
                    .timestamp(Instant.now())
                    .build();

            repository.save(log);
        }
    }

    private String clientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        return req.getRemoteAddr();
    }
}
