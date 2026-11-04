package com.bluebells.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bluebellsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Bluebells School LMS & Management Portal API")
                        .description("Production REST API documentation for Bluebells Schooling System in Hyderabad, Sindh, Pakistan.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Bluebells School Administration")
                                .email("Bluebells.schoolingsystem@gmail.com")
                                .url("https://bluebellsschooling7.wixsite.com/bluebellsschoolingsy"))
                        .license(new License().name("Proprietary - Bluebells Schooling System")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter JWT Bearer token to authorize protected API endpoints.")));
    }
}
