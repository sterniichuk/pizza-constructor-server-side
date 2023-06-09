package com.pizzacalculator.pizzacalculatorserverside.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:3000/", "http://localhost:3000/*","http://localhost:3000/**") // Add the allowed origin (your frontend URL)
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Add the all
                .maxAge(2020020202)
                .allowedHeaders("Authorization", "Cache-Control", "Content-Type", "Access-Control-Allow-Credentials")// owed HTTP methods
                .allowCredentials(true); // Allow sending cookies from the frontend
    }
}

