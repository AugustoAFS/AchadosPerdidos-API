package com.AchadosPerdidos.API.Application.Services.Auth;

import com.AchadosPerdidos.API.Application.DTOs.Response.Auth.OAuthUserDTO;
import com.AchadosPerdidos.API.Application.Interfaces.Auth.IOAuthProviderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class GoogleAuthService implements IOAuthProviderService {

    private static final Logger logger = LoggerFactory.getLogger(GoogleAuthService.class);

    @Value("${google.auth.client-id}")
    private String clientId;

    @Value("${google.auth.client-secret}")
    private String clientSecret;

    @Value("${google.auth.redirect-uri}")
    private String redirectUri;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GoogleAuthService() {
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String generateAuthorizationUrl() {
        try {
            String authUrl = "https://accounts.google.com/o/oauth2/auth?" +
                    "client_id=" + URLEncoder.encode(clientId, StandardCharsets.UTF_8) + "&" +
                    "redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8) + "&" +
                    "response_type=code&" +
                    "scope=openid%20email%20profile&" +
                    "access_type=offline&" +
                    "prompt=consent";

            logger.info("URL de autorização construída: {}", authUrl);
            return authUrl;
        } catch (Exception e) {
            logger.error("Erro ao construir URL de autorização", e);
            throw new RuntimeException("Erro ao construir URL de autorização", e);
        }
    }

    @Override
    public OAuthUserDTO exchangeCodeForUserInfo(String authorizationCode) {
        try {
            logger.info("Iniciando processo de obtenção de informações do usuário com código: {}", authorizationCode);
            String accessToken = getAccessToken(authorizationCode);

            return getUserInfo(accessToken);

        } catch (RuntimeException e) {
            logger.error("Erro ao processar autenticação do Google", e);
            throw new RuntimeException("Erro ao processar autenticação do Google", e);
        }
    }

    private String getAccessToken(String code) {
        try {
            String tokenUrl = "https://oauth2.googleapis.com/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("code", code);
            body.add("client_id", clientId);
            body.add("client_secret", clientSecret);
            body.add("redirect_uri", redirectUri);
            body.add("grant_type", "authorization_code");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            logger.info("Enviando requisição de token para: {}", tokenUrl);
            ResponseEntity<String> response = restTemplate.postForEntity(tokenUrl, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Erro ao obter token. Status: {}, Conteúdo: {}",
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException("Erro ao obter token: " + response.getBody());
            }

            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String accessToken = jsonNode.get("access_token").asText();

            if (accessToken == null || accessToken.isEmpty()) {
                logger.error("Token de acesso é nulo ou vazio");
                throw new RuntimeException("Token de acesso inválido");
            }

            logger.info("Token de acesso obtido com sucesso");
            return accessToken;

        } catch (JsonProcessingException | RuntimeException e) {
            logger.error("Erro ao obter token de acesso", e);
            throw new RuntimeException("Erro ao obter token de acesso", e);
        }
    }

    private OAuthUserDTO getUserInfo(String accessToken) {
        try {
            String userInfoUrl = "https://www.googleapis.com/oauth2/v2/userinfo";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> request = new HttpEntity<>(headers);

            logger.info("Enviando requisição de informações do usuário");
            ResponseEntity<String> response = restTemplate.exchange(
                    userInfoUrl, HttpMethod.GET, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                logger.error("Erro ao obter informações do usuário. Status: {}, Conteúdo: {}",
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException("Erro ao obter informações do usuário: " + response.getBody());
            }

            JsonNode userInfoJson = objectMapper.readTree(response.getBody());

            if (userInfoJson.isNull()) {
                logger.error("Resposta de informações do usuário é nula");
                throw new RuntimeException("Não foi possível obter informações do usuário");
            }

            OAuthUserDTO user = new OAuthUserDTO();
            user.setProviderId(userInfoJson.get("id").asText());
            user.setProvider("google");
            user.setEmail(userInfoJson.get("email").asText());
            user.setName(userInfoJson.get("name").asText());
            user.setPictureUrl(userInfoJson.get("picture").asText());
            user.setEmailVerified(userInfoJson.get("verified_email").asBoolean());

            logger.info("Informações do usuário obtidas com sucesso: {}", user.getEmail());
            return user;

        } catch (JsonProcessingException | RuntimeException e) {
            logger.error("Erro ao obter informações do usuário", e);
            throw new RuntimeException("Erro ao obter informações do usuário", e);
        }
    }

    @Override
    public String getRedirectUrl() {
        return redirectUri;
    }
}

