package com.AchadosPerdidos.API.Application.Config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    private final EnvironmentConfig environmentConfig;

    @Value("${SWAGGER_SERVER_URL:}")
    private String swaggerServerUrl;

    public SwaggerConfig(EnvironmentConfig environmentConfig) {
        this.environmentConfig = environmentConfig;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(buildServer()))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", jwtScheme()));
    }

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/api/**")
                .packagesToScan("com.AchadosPerdidos.API.Presentation.Controller")
                .build();
    }

    // ===================== helpers =====================

    private Server buildServer() {
        return new Server()
                .url(resolveServerUrl())
                .description(environmentConfig.isProduction()
                        ? "Servidor de Produção"
                        : "Servidor de Desenvolvimento");
    }

    private String resolveServerUrl() {
        if (!swaggerServerUrl.isBlank()) {
            return swaggerServerUrl;
        }

        String doDomain = System.getenv("DIGITALOCEAN_APP_DOMAIN");
        if (doDomain != null && !doDomain.isBlank()) {
            return doDomain.startsWith("http") ? doDomain : "https://" + doDomain;
        }

        return environmentConfig.isProduction()
                ? "https://api-achadosperdidos.com.br"
                : "http://localhost:8080";
    }

    private Info apiInfo() {
        return new Info()
                .title("API Achados e Perdidos")
                .version("1.0.0")
                .description("API para sistema de achados e perdidos")
                .contact(new Contact()
                        .name("Equipe de Desenvolvimento")
                        .email("contato@achadosperdidos.com.br"))
                .license(new License()
                        .name("MIT")
                        .url("https://opensource.org/licenses/MIT"));
    }

    private SecurityScheme jwtScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }
}