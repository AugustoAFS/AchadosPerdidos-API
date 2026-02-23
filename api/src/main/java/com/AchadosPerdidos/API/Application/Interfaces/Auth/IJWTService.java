package com.AchadosPerdidos.API.Application.Interfaces.Auth;

import com.AchadosPerdidos.API.Application.DTOs.Response.Auth.TokenValidationDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.User.UsuariosDTO;

public interface IJWTService {

    String createToken(String email, String name, String role, String userId);

    boolean validateToken(String token);

    String getEmailFromToken(String token);

    String getRoleFromToken(String token);

    String getUserIdFromToken(String token);

    UsuariosDTO getUsuarioFromToken(String token);

    String extractToken(String authorizationHeader);

    String logout(String authorizationHeader);

    TokenValidationDTO validateTokenAndGetInfo(String authorizationHeader);
}
