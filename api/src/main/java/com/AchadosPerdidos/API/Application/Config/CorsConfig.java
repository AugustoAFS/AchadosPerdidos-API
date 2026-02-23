package com.AchadosPerdidos.API.Application.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class CorsConfig {

    private final EnvironmentConfig environmentConfig;

    @Value("${SECURITY_ALLOWED_ORIGINS:}")
    private List<String> allowedOrigins;

    @Value("${SECURITY_ALLOWED_ORIGIN_PATTERNS:http://localhost:*}")
    private List<String> allowedOriginPatterns;

    public CorsConfig(EnvironmentConfig environmentConfig) {
        this.environmentConfig = environmentConfig;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Content-Type", "X-Token-Expiry"));
        config.setMaxAge(3600L);

        if (environmentConfig.isProduction()) {
            config.setAllowedOrigins(allowedOrigins);
        } else {
            config.setAllowedOriginPatterns(allowedOriginPatterns);
        }

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}