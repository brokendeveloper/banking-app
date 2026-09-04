package com.brokendev.backend.infra.cors;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:3001", "http://localhost:8081")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS" )
                .allowedHeaders("*")
                .allowCredentials(true);

        registry.addMapping("/swagger-ui/**").allowedOrigins("*");
        registry.addMapping("/v3/api-docs/**").allowedOrigins("*");
    }
}
