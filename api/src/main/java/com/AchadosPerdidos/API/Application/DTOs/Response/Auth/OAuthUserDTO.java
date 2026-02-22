package com.AchadosPerdidos.API.Application.DTOs.Response.Auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuthUserDTO {

    private String providerId;
    private String provider;
    private String email;
    private String name;
    private String pictureUrl;
    private boolean emailVerified;
}
