package com.tps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(info = @Info(title = "Trade Post Firms API", version = "v1", description = "Endpoints for firms, challenges, and filters"))
@SpringBootApplication
@EnableAsync
@EnableJpaAuditing
@EnableCaching
public class TradingPostServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradingPostServiceApplication.class, args);
	}

}
