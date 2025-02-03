package com.pakal.cloud.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
            .info(new Info()
                .title("Blog Form API")
                .description("API for managing blog forms")
                .version("1.0")
                .contact(new Contact()
                    .name("Emmanuel")
                    .email("emmanuel@pakal.cloud")))
            .components(new Components()
                .addSecuritySchemes("basicAuth", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("basic")));
    }
}