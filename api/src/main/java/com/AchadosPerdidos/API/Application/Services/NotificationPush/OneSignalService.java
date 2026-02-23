package com.AchadosPerdidos.API.Application.Services.NotificationPush;

import com.AchadosPerdidos.API.Application.Config.OneSignalConfig;
import com.AchadosPerdidos.API.Application.Interfaces.Notification.IPushNotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OneSignalService implements IPushNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(OneSignalService.class);

    private final OneSignalConfig config;
    private final RestTemplate oneSignalRestTemplate;

    @Override
    public boolean sendToDevice(
            String deviceToken,
            String title,
            String message,
            Map<String, String> data) {
        if (!config.isEnabledAndConfigured()) {
            logger.debug("OneSignal desabilitado ou não configurado");
            return false;
        }

        if (!isValidToken(deviceToken)) {
            logger.warn("Token inválido");
            return false;
        }

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(buildPayload(
                    List.of(deviceToken),
                    title,
                    message,
                    data), buildHeaders());

            ResponseEntity<String> response = oneSignalRestTemplate.exchange(
                    config.getApiUrl(),
                    HttpMethod.POST,
                    request,
                    String.class);

            return response.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            logger.error("Erro ao enviar notificação OneSignal", e);
            return false;
        }
    }

    @Override
    public boolean sendToMultipleDevices(
            List<String> deviceTokens,
            String title,
            String message,
            Map<String, String> data) {
        if (!config.isEnabledAndConfigured()) {
            logger.debug("OneSignal desabilitado ou não configurado");
            return false;
        }

        if (deviceTokens == null || deviceTokens.isEmpty()) {
            logger.warn("Lista de tokens vazia");
            return false;
        }

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(
                    buildPayload(deviceTokens, title, message, data),
                    buildHeaders());

            ResponseEntity<String> response = oneSignalRestTemplate.exchange(
                    config.getApiUrl(),
                    HttpMethod.POST,
                    request,
                    String.class);

            return response.getStatusCode().is2xxSuccessful();

        } catch (Exception e) {
            logger.error("Erro ao enviar notificação OneSignal para múltiplos dispositivos", e);
            return false;
        }
    }

    @Override
    public boolean isAvailable() {
        return config.isEnabledAndConfigured();
    }

    /*
     * =======================
     * Helpers privados
     * =======================
     */

    private Map<String, Object> buildPayload(
            List<String> tokens,
            String title,
            String message,
            Map<String, String> data) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("app_id", config.getAppId());
        payload.put("include_player_ids", tokens);
        payload.put("headings", Map.of("en", title));
        payload.put("contents", Map.of("en", message));

        if (data != null && !data.isEmpty()) {
            payload.put("data", data);
        }

        return payload;
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String auth = Base64.getEncoder()
                .encodeToString((config.getRestApiKey() + ":")
                        .getBytes(StandardCharsets.UTF_8));

        headers.set("Authorization", "Basic " + auth);
        return headers;
    }

    private boolean isValidToken(String token) {
        return token != null && token.length() > 20 && token.length() < 200;
    }
}
