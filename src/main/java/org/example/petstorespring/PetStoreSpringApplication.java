package org.example.petstorespring;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("org.example.petstorespring.persistence")
public class PetStoreSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetStoreSpringApplication.class, args);
	}

}
