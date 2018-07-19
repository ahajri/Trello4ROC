package com.roc.trello;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration()
public class Trello4RocApplication {

	public static void main(String[] args) {
		SpringApplication.run(Trello4RocApplication.class, args);
	}

	
}
