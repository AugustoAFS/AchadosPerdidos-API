package com.AchadosPerdidos.API.Application.Services;

import com.AchadosPerdidos.API.Application.DTOs.Auth.TokenValidationDTO;
import com.AchadosPerdidos.API.Application.DTOs.Usuario.UsuariosDTO;
import com.AchadosPerdidos.API.Application.Exeptions.ValidationException;
import com.AchadosPerdidos.API.Application.Services.Interfaces.IJWTService;
import com.AchadosPerdidos.API.Application.Services.Interfaces.IUsuariosService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JWTService implements IJWTService {

    private static final Logger log = LoggerFactory.getLogger(JWTService.class);

    private final SecretKey secretKey;
    private final IUsuariosService usuariosService;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.audience}")
    private String audience;

    @Value("${jwt.expiry-in-minutes}")
    private int expiryMinutes;

    public JWTService(
            SecretKey secretKey,
            @Lazy IUsuariosService usuariosService
    ) {
        this.secretKey = secretKey;
        this.usuariosService = usuariosService;
    }

    @Override
    public String createToken(String email, String name, String role, String userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expiryMinutes * 60L * 1000);

        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);
        claims.put("name", name);
        claims.put("role", role);

        return Jwts.builder()
                .issuer(issuer)
                .audience().add(audience).and()
                .subject(userId)
                .issuedAt(now)
                .expiration(expiry)
                .claims(claims)
                .id(UUID.randomUUID().toString())
                .signWith(secretKey)
                .compact();
    }

    @Override
    @Cacheable(value = "jwtTokens", key = "#token", unless = "#result == false")
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public String getEmailFromToken(String token) {
        return getClaims(token).get("email", String.class);
    }

    @Override
    public String getRoleFromToken(String token) {
        return getClaims(token).get("role", String.class);
    }

    @Override
    public String getUserIdFromToken(String token) {
        return getClaims(token).getSubject();
    }

    @Override
    public UsuariosDTO getUsuarioFromToken(String token) {
        String email = getEmailFromToken(token);
        return usuariosService.getUsuarioByEmail(email);
    }

    @Override
    public String extractToken(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ValidationException("Header Authorization inválido");
        }

        return authorizationHeader.substring(7).trim();
    }

    @Override
    public String logout(String authorizationHeader) {
        extractToken(authorizationHeader);
        return "Logout realizado com sucesso. Descarte o token localmente.";
    }

    @Override
    public TokenValidationDTO validateTokenAndGetInfo(String authorizationHeader) {
        String token = extractToken(authorizationHeader);

        if (!validateToken(token)) {
            throw new ValidationException("Token inválido ou expirado");
        }

        return new TokenValidationDTO(
                true,
                getEmailFromToken(token),
                getRoleFromToken(token),
                "Token válido"
        );
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}