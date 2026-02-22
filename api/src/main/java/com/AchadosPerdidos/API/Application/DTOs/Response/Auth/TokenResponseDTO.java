package com.AchadosPerdidos.API.Application.DTOs.Response.Auth;

import lombok.Data;

@Data
public class TokenResponseDTO {

    private String token;
    private String tokenType = "Bearer";
    private long expiresInMinutes;
    private String email;
    private String role;
}
