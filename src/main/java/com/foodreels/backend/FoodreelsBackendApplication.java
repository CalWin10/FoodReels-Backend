package com.foodreels.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FoodreelsBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodreelsBackendApplication.class, args);
	}

}
