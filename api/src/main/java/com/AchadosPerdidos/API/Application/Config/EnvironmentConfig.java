package com.AchadosPerdidos.API.Application.Config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class EnvironmentConfig
        implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private Environment environment;

    @Override
    public void onApplicationEvent(
            @NonNull ApplicationEnvironmentPreparedEvent event) {

        this.environment = event.getEnvironment();
        ConfigurableEnvironment env = (ConfigurableEnvironment) environment;

        String activeProfile = getActiveProfile();

        if (isProduction()) {
            return;
        }

        Dotenv dotenv = Dotenv.configure()
                .ignoreIfMissing()
                .ignoreIfMalformed()
                .load();

        Map<String, Object> properties = new HashMap<>();

        dotenv.entries().forEach(entry -> {
            properties.put(entry.getKey(), entry.getValue());
        });

        if (!properties.isEmpty()) {
            env.getPropertySources()
                    .addFirst(new MapPropertySource("dotenv", properties));
        }
    }

    public boolean isProduction() {
        return "prd".equalsIgnoreCase(getActiveProfile());
    }

    public boolean isDevelopment() {
        return "dev".equalsIgnoreCase(getActiveProfile());
    }

    public String getActiveProfile() {
        if (environment == null) {
            return "dev";
        }

        String[] profiles = environment.getActiveProfiles();
        if (profiles.length > 0) {
            return profiles[0];
        }

        return environment.getProperty("spring.profiles.active", "dev");
    }

    public Environment getEnvironment() {
        return environment;
    }
}