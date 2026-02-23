package com.AchadosPerdidos.API.Presentation.Controller;

import com.AchadosPerdidos.API.Application.DTOs.Request.User.LoginRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Request.User.RedefinirSenhaRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Auth.TokenResponseDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Auth.TokenValidationDTO;
import com.AchadosPerdidos.API.Application.Interfaces.Auth.IJWTService;
import com.AchadosPerdidos.API.Application.Interfaces.Auth.IOAuthProviderService;
import com.AchadosPerdidos.API.Application.Interfaces.IUsersService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Autenticação e autorização de usuários")
public class AuthController {

    @Autowired
    private IUsersService usersService;

    @Autowired
    @Qualifier("googleAuthService")
    private IOAuthProviderService googleAuthService;

    @Autowired
    private IJWTService jwtService;

    @PostMapping("/login")
    @Operation(summary = "Login com e-mail e senha", description = "Autentica o usuário e retorna um token JWT")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(usersService.login(dto));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout", description = "Invalida o token JWT. O cliente deve descartá-lo localmente")
    public ResponseEntity<String> logout(
            @Parameter(description = "Token JWT no formato 'Bearer {token}'") @RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(jwtService.logout(authorization));
    }

    @GetMapping("/validate")
    @Operation(summary = "Validar token JWT", description = "Confirma se o token é válido e retorna os claims básicos")
    public ResponseEntity<TokenValidationDTO> validateToken(
            @Parameter(description = "Token JWT no formato 'Bearer {token}'") @RequestHeader("Authorization") String authorization) {
        return ResponseEntity.ok(jwtService.validateTokenAndGetInfo(authorization));
    }

    @PostMapping("/redefinir-senha")
    @Operation(summary = "Redefinir senha", description = "Redefine a senha do usuário informado")
    public ResponseEntity<String> redefinirSenha(@Valid @RequestBody RedefinirSenhaRequestDTO dto) {
        usersService.redefinirSenha(dto);
        return ResponseEntity.ok("Senha redefinida com sucesso");
    }

    @GetMapping("/google/login")
    @Operation(summary = "Iniciar login com Google", description = "Redireciona para a página de autorização do Google OAuth2")
    public ResponseEntity<Void> loginGoogle() {
        String authUrl = googleAuthService.generateAuthorizationUrl();
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", authUrl).build();
    }

    @GetMapping("/google/callback")
    @Operation(summary = "Callback do Google OAuth2", description = "Processa o código de autorização e retorna token JWT")
    public ResponseEntity<TokenResponseDTO> handleGoogleCallback(
            @Parameter(description = "Código de autorização retornado pelo Google") @RequestParam String code) {
        return ResponseEntity.ok(usersService.loginWithGoogle(code, googleAuthService, jwtService));
    }
}
