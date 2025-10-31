package com.tps.model;
 

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "api_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clientId;
    private String ipAddress;
    private String httpMethod;
    private String endpoint;
    private int statusCode;
    private long durationMs;
    private Instant timestamp;
}

