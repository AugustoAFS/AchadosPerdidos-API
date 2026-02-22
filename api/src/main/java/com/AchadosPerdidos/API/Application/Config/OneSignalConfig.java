package com.AchadosPerdidos.API.Application.Config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Getter
@Configuration
public class OneSignalConfig {

    @Value("${onesignal.app-id:}")
    private String appId;

    @Value("${onesignal.rest-api-key:}")
    private String restApiKey;

    @Value("${onesignal.enabled:false}")
    private boolean enabled;

    @Value("${onesignal.api-url:https://api.onesignal.com/notifications}")
    private String apiUrl;

    @Bean
    public RestTemplate oneSignalRestTemplate() {
        return new RestTemplate();
    }

    public boolean isEnabledAndConfigured() {
        return enabled
                && appId != null && !appId.isBlank()
                && restApiKey != null && !restApiKey.isBlank();
    }
}