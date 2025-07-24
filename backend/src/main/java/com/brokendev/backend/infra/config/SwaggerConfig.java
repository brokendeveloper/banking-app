package com.brokendev.backend.infra.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Bank App")
                        .version("1.0.0")
                        .description("API documentation for Bank App and all your operations")
                        .contact(new Contact()
                                .name("Luccas Fernandes - BrokenDeveloper")
                                .email("contatoluccasf9@gmail.com")
                                .url("https://github.com/brokendeveloper/banking-app")
                        )
                )
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Server")
                ));
    }
}