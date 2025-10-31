package com.tps.config;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tps.service.TokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	private final TokenService tokenService;

	public JwtAuthFilter(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String auth = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (auth != null && auth.startsWith("Bearer ")) {
			String token = auth.substring(7);
			try {
				Jws<Claims> jws = tokenService.parse(token);
				String ipInToken = jws.getBody().get("ip", String.class);
				String clientIp = clientIp(request);
				if (clientIp != null && clientIp.startsWith("::ffff:"))
					clientIp = clientIp.substring(7);
				if (!clientIp.equals(ipInToken)) {
					throw new RuntimeException("IP mismatch");
				}
				Authentication authentication = new AbstractAuthenticationToken(
						List.of(new SimpleGrantedAuthority("ROLE_USER"))) {
					@Override
					public Object getCredentials() {
						return token;
					}

					@Override
					public Object getPrincipal() {
						return ipInToken;
					}
				};
				((AbstractAuthenticationToken) authentication).setAuthenticated(true);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (Exception ignored) {
				// Invalid token -> let Security chain handle 401/403 later
			}
		}
		filterChain.doFilter(request, response);
	}

	private String clientIp(HttpServletRequest req) {
		String xff = req.getHeader("X-Forwarded-For");
		if (xff != null && !xff.isBlank())
			return xff.split(",")[0].trim();
		return req.getRemoteAddr();
	}
}