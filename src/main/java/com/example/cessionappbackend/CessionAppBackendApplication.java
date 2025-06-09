package com.example.cessionappbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class CessionAppBackendApplication {

	public static void main(String[] args) {
		// Load .env file before starting Spring Boot
		Dotenv dotenv = Dotenv.configure()
			.ignoreIfMissing()
			.systemProperties()
			.load();

		SpringApplication.run(CessionAppBackendApplication.class, args);
	}

}

