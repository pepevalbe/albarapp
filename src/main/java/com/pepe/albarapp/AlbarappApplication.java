package com.pepe.albarapp;

import com.pepe.albarapp.service.document.aecoc.AecocConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class AlbarappApplication implements WebMvcConfigurer {

	@Value("${albarapp.aecoc_constants}")
	String aecocConstants;

	public static void main(String[] args) {
		SpringApplication.run(AlbarappApplication.class, args);
	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**").allowedOrigins("http://localhost:8080").allowedMethods("*");
	}

	@Bean
	public void initAecocConstants() {
		AecocConstants.init(aecocConstants);
	}
}
