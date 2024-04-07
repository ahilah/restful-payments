package com.payments.restpayments;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    // http://localhost:8080/swagger-ui/index.html

    @Bean
    public GroupedOpenApi roleApi() {
        return GroupedOpenApi.builder()
                .group("Role API")
                .pathsToMatch("/role/**")
                .build();
    }

    @Bean
    public GroupedOpenApi superAdminApi() {
        return GroupedOpenApi.builder()
                .group("Super Admin API")
                .pathsToMatch("/super/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("Admin API")
                .pathsToMatch("/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi clientApi() {
        return GroupedOpenApi.builder()
                .group("Client API")
                .pathsToMatch("/client/**")
                .build();
    }

    @Bean
    public GroupedOpenApi creditCardApi() {
        return GroupedOpenApi.builder()
                .group("Credit Card API")
                .pathsToMatch("/card/**")
                .build();
    }

    @Bean
    public GroupedOpenApi accountApi() {
        return GroupedOpenApi.builder()
                .group("Account API")
                .pathsToMatch("/account/**")
                .build();
    }

    @Bean
    public GroupedOpenApi paymentApi() {
        return GroupedOpenApi.builder()
                .group("Payment API")
                .pathsToMatch("/payment/**")
                .build();
    }

    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("Version 1")
                .pathsToMatch("/v1/**")
                .build();
    }

    @Bean
    public GroupedOpenApi v2Api() {
        return GroupedOpenApi.builder()
                .group("Version 2")
                .pathsToMatch("/v2/**")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("Payments REST API")
                        .description("Simple API for Payments (Bank) system.")
                        .version("1.0").contact(
                                new Contact().name("Anastasiia Hileta")
                                        .email( "anastasiia.hileta@gmail.com")
                                        .url("https://github.com/ahilah"))
                        .license(new License().name("License of API")
                                .url("https://github.com/ahilah/restful-payments")));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }
}