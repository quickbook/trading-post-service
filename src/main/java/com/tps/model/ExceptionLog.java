package com.tps.model;

 

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "exception_logs", indexes = {
    @Index(name = "ix_exception_logs_time", columnList = "timestamp"),
    @Index(name = "ix_exception_logs_correlation", columnList = "correlationId")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ExceptionLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant timestamp;

    private String correlationId;

    private String clientId;       // from header or token
    private String ipAddress;

    private String httpMethod;
    private String path;
    private Integer statusCode;

    @Column(length = 2048)
    private String message;

    @Column(length = 8192)
    private String stackTrace;     // truncated to avoid giant rows

    private String exceptionType;  // e.g., IllegalArgumentException
}

