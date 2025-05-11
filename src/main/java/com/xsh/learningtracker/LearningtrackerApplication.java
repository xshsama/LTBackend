package com.xsh.learningtracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean; // Added for @Bean
import org.springframework.web.client.RestTemplate; // Added for RestTemplate

@SpringBootApplication
public class LearningtrackerApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningtrackerApplication.class, args);
	}

	@Bean // Define RestTemplate as a Spring Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
