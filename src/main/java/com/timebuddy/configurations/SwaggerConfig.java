package com.timebuddy.configurations;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SwaggerConfig class for configuring Swagger/OpenAPI documentation for the API.
 * This class sets up the title, version, and description for the API documentation
 * and configures JWT bearer token authentication for secure endpoints.
 */
@Configuration
public class SwaggerConfig {

    /**
     * Bean that configures the custom OpenAPI specification for the API.
     * The method sets the API title, version, description, and security scheme for authentication.
     *
     * @return An OpenAPI object containing the custom configuration for API documentation.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Cityatlas API") // API title
                        .version("1.0") // API version
                        .description("API for managing cities, countries, and continents")) // API description
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme() // Adding JWT security scheme
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer") // Authentication scheme type
                                .bearerFormat("JWT"))) // The format used for the JWT token
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth")); // Applying security requirement to endpoints
    }
}

