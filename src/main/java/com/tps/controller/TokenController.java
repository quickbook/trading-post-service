package com.tps.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tps.dto.TokenCacheEntry;
import com.tps.service.IpAllowlistService;
import com.tps.service.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/tradingpost/auth")
@Validated
@Slf4j
public class TokenController {
	
	private final IpAllowlistService ipAllowlist;
	private final TokenService tokens;
	
	@Value("${security.ip-allowlist}")
	private String allowList;
	
	public TokenController(IpAllowlistService ipAllowlist, TokenService tokens) {
		this.ipAllowlist = ipAllowlist;
		this.tokens = tokens;
	}

	@PostMapping("/token")
	public ResponseEntity<?> token(HttpServletRequest request) {
		String ip = clientIp(request);
		 log.info("Requested IP {}" , ip);
		if (!ipAllowlist.isAllowed(ip)) {
			 log.info("IP not in allowlist {}" , ip);
			return ResponseEntity.status(403)
					.body(Map.of("error", "forbidden_ip", "error_description", "IP not in allowlist","ipAllowlist",allowList,"requestedIP",ip));
		}
		TokenCacheEntry entry = tokens.issueOrReuseForIp(ip);
		long expiresIn = Duration.between(Instant.now(), entry.accessExpiry()).toSeconds();
		return ResponseEntity.ok(Map.of("token_type", "Bearer", "access_token", entry.accessToken(), "expires_in",
				expiresIn, "refresh_token", entry.refreshToken()));
	}

	public record RefreshRequest(@NotBlank String refreshToken) {
	}

	@PostMapping("/refresh")
	public ResponseEntity<?> refresh(@RequestBody RefreshRequest body, HttpServletRequest request) {
		String ip = clientIp(request);
		if (!ipAllowlist.isAllowed(ip)) {
			return ResponseEntity.status(403)
					.body(Map.of("error", "forbidden_ip", "error_description", "IP not in allowlist"));
		}
		try {
			TokenCacheEntry entry = tokens.refresh(ip, body.refreshToken());
			long expiresIn = Duration.between(Instant.now(), entry.accessExpiry()).toSeconds();
			return ResponseEntity.ok(Map.of("token_type", "Bearer", "access_token", entry.accessToken(), "expires_in",
					expiresIn, "refresh_token", entry.refreshToken()));
		} catch (Exception e) {
			return ResponseEntity.status(401)
					.body(Map.of("error", "invalid_refresh", "error_description", e.getMessage()));
		}
	}

	private String clientIp(HttpServletRequest req) {
		String xff = req.getHeader("X-Forwarded-For");
		if (xff != null && !xff.isBlank())
			return xff.split(",")[0].trim();
		return req.getRemoteAddr();
	}
}