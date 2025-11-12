package com.tps.config;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tps.dto.ApiResponse;
import com.tps.dto.ErrorDetails;
import com.tps.service.ExceptionLoggingService;
import com.tps.service.TokenService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	
	private final TokenService tokenService;
	private final ObjectMapper objectMapper;
	private final ExceptionLoggingService exceptionLogger;

	public JwtAuthFilter(TokenService tokenService, ObjectMapper objectMapper, ExceptionLoggingService exceptionLogger) {
		this.tokenService = tokenService;
		this.objectMapper = objectMapper;
		this.exceptionLogger = exceptionLogger;
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
				String roleName = jws.getBody().get("role", String.class);
                if (roleName == null) {
                    roleName = "USER"; // Default to USER if role is missing
                }
				String clientIp = clientIp(request);
				if (clientIp != null && clientIp.startsWith("::ffff:"))
					clientIp = clientIp.substring(7);
				if (!clientIp.equals(ipInToken)) {
					throw new JwtException("IP mismatch");
				}
				// Use the extracted role to create the authority list
                List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + roleName.toUpperCase()));
				
                Authentication authentication = new AbstractAuthenticationToken(authorities) {
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
			} catch (JwtException ex) {
				// Invalid token -> let Security chain handle 401/403 later
				
				exceptionLogger.save(request, ex, HttpServletResponse.SC_UNAUTHORIZED, clientIp(request));
				ErrorDetails details = new ErrorDetails("TOKEN_INVALID", ex.getMessage(), null, ex.getClass().getSimpleName());
				ApiResponse<Object> apiResponse = ApiResponse.<Object>builder()
						.success(false)
						.message("JWT Token is invalid or expired")
						.data(null)
						.errorDetails(details)
						.status(HttpStatus.UNAUTHORIZED)
						.path(request.getRequestURI())
						.timestamp(System.currentTimeMillis())
						.build();
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType(MediaType.APPLICATION_JSON_VALUE);
				response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
				
				return;
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