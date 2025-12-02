package com.tps.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    // BCrypt password encoder
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Main security filter chain. Uses http.cors(...) and ensures OPTIONS is permitted early.
     */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            // cors configured below
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())

            // stateless session (we use JWT)
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // authorization rules
            .authorizeHttpRequests(reg -> reg
                // Swagger & docs
                .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/openapi.yaml").permitAll()

                // Allow CORS preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.GET,  "/api/whatsapp/**").permitAll()
    		    .requestMatchers(HttpMethod.POST, "/api/whatsapp/**").permitAll()

                // Auth endpoints
                .requestMatchers("/tradingpost/auth/**").permitAll()

                // Example fine-grained rules (adjust as needed)
                .requestMatchers(HttpMethod.GET, "/tradingpost/api/v1/firms/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/tradingpost/api/v1/challenges/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/tradingpost/api/v1/reviews/**").authenticated()
                
                .requestMatchers(HttpMethod.POST, "/tradingpost/api/v1/firms/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/tradingpost/api/v1/challenges/**").authenticated()
                .requestMatchers(HttpMethod.POST, "/tradingpost/api/v1/reviews/**").authenticated()
               
 
                .requestMatchers(HttpMethod.PUT, "/tradingpost/api/v1/firms/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/tradingpost/api/v1/reviews/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/tradingpost/api/v1/challenges/**").authenticated()
                
                .requestMatchers(HttpMethod.DELETE, "/tradingpost/api/v1/firms/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/tradingpost/api/v1/reviews/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/tradingpost/api/v1/challenges/**").authenticated()
                
                .requestMatchers(HttpMethod.PATCH, "/tradingpost/api/v1/firms/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/tradingpost/api/v1/reviews/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/tradingpost/api/v1/challenges/**").authenticated()
           

                .requestMatchers("/tradingpost/api/v1/users/**").authenticated()

                // everything else authenticated
                .anyRequest().authenticated()
            )
            .exceptionHandling(eh -> eh.accessDeniedHandler(accessDeniedHandler))

            // add JWT filter (before UsernamePasswordAuthenticationFilter)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Strong CORS configuration. Add allowed origins and/or allowedOriginPatterns.
     * Note: if you set allowCredentials(true) you cannot use "*" for origins.
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Exact origins you want to allow (add staging/production)
        config.setAllowedOrigins(List.of(
            "http://localhost:5173",
            "https://dev01-ui.pranalyticx.cloud"
        ));

        // If you need wildcard patterns (subdomains), prefer allowedOriginPatterns:
        // config.setAllowedOriginPatterns(List.of("http://localhost:*", "https://*.pranalyticx.cloud"));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Allow headers commonly needed (Authorization is critical)
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        // Expose headers to browser if backend sends them
        config.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply to all endpoints
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    /**
     * Register a CorsFilter with highest precedence so it runs BEFORE other filters (including JWT).
     * This helps ensure preflight (OPTIONS) requests are handled and CORS headers applied early.
     *
     * Note: call the local corsConfigurationSource() directly to avoid autowiring ambiguity
     * (Spring Boot registers other CorsConfigurationSource beans which caused the earlier error).
     */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public FilterRegistrationBean<CorsFilter> corsFilterRegistrationBean() {
        CorsFilter corsFilter = new CorsFilter(corsConfigurationSource()); // call directly, no ambiguity
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(corsFilter);
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
