package com.insta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InstaUploaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(InstaUploaderApplication.class, args);
	}
}