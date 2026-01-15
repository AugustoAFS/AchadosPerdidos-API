package com.AchadosPerdidos.API.Application.Config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Getter
@Configuration
public class OneSignalConfig {

    @Value("${ONESIGNAL_APP_ID:}")
    private String appId;

    @Value("${ONESIGNAL_REST_API_KEY:}")
    private String restApiKey;

    @Value("${ONESIGNAL_ENABLED:false}")
    private boolean enabled;

    @Value("${ONESIGNAL_API_URL:https://api.onesignal.com/notifications}")
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