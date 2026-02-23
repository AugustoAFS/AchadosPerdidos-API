package com.AchadosPerdidos.API.Application.Interfaces;

import com.AchadosPerdidos.API.Application.DTOs.Request.User.LoginRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Request.User.RedefinirSenhaRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Auth.TokenResponseDTO;
import com.AchadosPerdidos.API.Application.Interfaces.Auth.IJWTService;
import com.AchadosPerdidos.API.Application.Interfaces.Auth.IOAuthProviderService;
import com.AchadosPerdidos.API.Domain.Entity.Users;

import java.util.List;
import java.util.Optional;

public interface IUsersService extends IBaseService<Users, Integer> {

    Optional<Users> findByEmail(String email);

    List<Users> findByCampus(Integer campusId);

    boolean existsByEmail(String email);

    TokenResponseDTO login(LoginRequestDTO dto);

    void redefinirSenha(RedefinirSenhaRequestDTO dto);

    TokenResponseDTO loginWithGoogle(String code, IOAuthProviderService oAuthProvider, IJWTService jwtService);
}
