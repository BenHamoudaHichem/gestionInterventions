package com.app.gestionInterventions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
public class GestionInterventionsApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionInterventionsApplication.class, args);
	}

}
