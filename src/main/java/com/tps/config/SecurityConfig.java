package com.tps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Bean
  SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
	  http
      .csrf(csrf -> csrf.disable())
      .cors(cors -> {}) // if you use CORS
      .authorizeHttpRequests(reg -> reg
        // Swagger / OpenAPI
        .requestMatchers(
          "/v3/api-docs/**",
          "/swagger-ui/**",
          "/swagger-ui.html"
        ).permitAll()
        // Preflight (CORS)
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
 
        // (If yours is under /firms/filter-options, permit that too)
        .requestMatchers("/api/v1/firms/**").permitAll()

        // everything else requires auth
        .anyRequest().authenticated()
      );

    return http.build();
  }
}

