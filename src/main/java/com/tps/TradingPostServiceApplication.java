package com.tps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
		  info = @Info(
		    title = "Trade Post Firms API",
		    version = "v1",
		    description = "Endpoints for firms, challenges, and filters"
		  )
		)
@SpringBootApplication
public class TradingPostServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradingPostServiceApplication.class, args);
	}

}
