package com.example.rickandmorty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RickandmortyApplication {

	public static void main(String[] args) {
        java.util.Locale.setDefault(java.util.Locale.ENGLISH);
        SpringApplication.run(RickandmortyApplication.class, args);
	}

}
