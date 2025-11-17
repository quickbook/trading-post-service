package com.tps.config;

import java.time.ZoneId;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.annotation.PostConstruct;

@Configuration
public class JacksonConfig {

	@Value("${app.timezone:UTC}")
	private String appTimeZone;

	@PostConstruct
	public void init() {
		TimeZone tz = TimeZone.getTimeZone(ZoneId.of(appTimeZone));
		TimeZone.setDefault(tz);
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.findAndRegisterModules(); // registers JavaTimeModule automatically
		mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		mapper.setTimeZone(TimeZone.getTimeZone(appTimeZone));
		return mapper;
	}
}
