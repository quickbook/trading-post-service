package com.tps.dto;

import java.time.Instant;

public record TokenCacheEntry(String accessToken, String refreshToken, Instant accessExpiry, Instant refreshExpiry) {
}