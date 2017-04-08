package org.aigor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class ReactiveBigDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReactiveBigDataApplication.class, args);
	}
}
