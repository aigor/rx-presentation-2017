package org.aigor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
public class ReactiveBigDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveBigDataApplication.class, args);
	}
}
