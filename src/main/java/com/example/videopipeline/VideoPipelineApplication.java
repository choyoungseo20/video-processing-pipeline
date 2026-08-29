package com.example.videopipeline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VideoPipelineApplication {

	public static void main(String[] args) {
		SpringApplication.run(VideoPipelineApplication.class, args);
	}

}
