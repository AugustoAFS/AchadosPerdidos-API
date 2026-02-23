package com.AchadosPerdidos.API.Application.DTOs.Response.Auth;

import lombok.Data;

@Data
public class TokenValidationDTO {

    private boolean valid;
    private String userId;
    private String email;
    private String name;
    private String role;
    private String message;
}
