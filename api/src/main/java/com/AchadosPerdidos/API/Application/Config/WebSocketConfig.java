package com.AchadosPerdidos.API.Application.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${WS_ENDPOINT:/ws}")
    private String endpoint;

    @Value("${WS_ALLOWED_ORIGINS:*}")
    private String allowedOrigins;

    @Value("${WS_APP_DEST_PREFIX:/app}")
    private String appPrefix;

    @Value("${WS_BROKER_PREFIX:/topic}")
    private String brokerPrefix;

    @Value("${WS_ENABLE_SOCKJS:true}")
    private boolean enableSockJs;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(brokerPrefix);
        registry.setApplicationDestinationPrefixes(appPrefix);
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        var endpointRegistration = registry
                .addEndpoint(endpoint)
                .setAllowedOriginPatterns(parse(allowedOrigins));

        if (enableSockJs) {
            endpointRegistration.withSockJS();
        }
    }

    private String[] parse(String value) {
        return value == null || value.isBlank()
                ? new String[]{"*"}
                : value.split(",");
    }
}