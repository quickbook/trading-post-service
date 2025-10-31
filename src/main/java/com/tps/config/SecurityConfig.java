package com.tps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();// password Encoder
	}

	private final JwtAuthFilter jwtAuthFilter;

	public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
		this.jwtAuthFilter = jwtAuthFilter;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		http
				// Disable CSRF (since we use JWT)
				.csrf(csrf -> csrf.disable())

				// Enable CORS for frontend apps (React, Angular, etc.)
				.cors(cors -> {
				})

				// Make the app stateless (no sessions)
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				// Define endpoint access rules
				.authorizeHttpRequests(reg -> reg
						// Allow Swagger / OpenAPI docs
						.requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/openapi.yaml")
						.permitAll()

						// Allow CORS preflight requests
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers(HttpMethod.OPTIONS, "/tradingpost/**").permitAll()

						// Allow token issue and refresh endpoints (public)
						.requestMatchers("/tradingpost/auth/**").permitAll()

						// Allow public APIs
						.requestMatchers("/tradingpost/api/v1/firms/**").authenticated()

						// Require authentication for user-related endpoints
						.requestMatchers("/tradingpost/api/v1/users/**").authenticated()

						// All other endpoints must also be authenticated
						.anyRequest().authenticated())

				// Add custom JWT validation filter
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}
