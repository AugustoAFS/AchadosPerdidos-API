package com.AchadosPerdidos.API.Presentation.Controller;

import com.AchadosPerdidos.API.Application.DTOs.Request.User.CreateUserRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Request.User.UpdateUserRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.User.UsuariosDTO;
import com.AchadosPerdidos.API.Application.Mapper.UsersMapper;
import com.AchadosPerdidos.API.Application.Services.UsersService;
import com.AchadosPerdidos.API.Domain.Entity.Users;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Gerenciamento de usuários")
public class UserController {

    @Autowired
    private UsersService usersService;

    @Autowired
    private UsersMapper usersMapper;

    @PostMapping
    @Operation(summary = "Criar usuário", description = "Cadastra um novo usuário no sistema")
    public ResponseEntity<UsuariosDTO> create(@Valid @RequestBody CreateUserRequestDTO dto) {
        Users user = usersMapper.fromCreate(dto);
        Users created = usersService.createWithCampuses(user, dto.getCampusIds());
        UsuariosDTO response = usersMapper.toResponse(created);
        response.setCampusIds(dto.getCampusIds());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuário por ID")
    public ResponseEntity<UsuariosDTO> findById(
            @Parameter(description = "ID do usuário") @PathVariable Integer id) {
        Users user = usersService.findById(id);
        UsuariosDTO response = usersMapper.toResponse(user);
        response.setCampusIds(usersService.getCampusIdsForUser(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os usuários ativos")
    public ResponseEntity<List<UsuariosDTO>> findAll() {
        List<Users> users = usersService.findAll();
        List<UsuariosDTO> dtos = users.stream().map(u -> {
            UsuariosDTO dto = usersMapper.toResponse(u);
            dto.setCampusIds(usersService.getCampusIdsForUser(u.getId()));
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/campus/{campusId}")
    @Operation(summary = "Listar usuários por campus")
    public ResponseEntity<List<UsuariosDTO>> findByCampus(
            @Parameter(description = "ID do campus") @PathVariable Integer campusId) {
        List<Users> users = usersService.findByCampus(campusId);
        List<UsuariosDTO> dtos = users.stream().map(u -> {
            UsuariosDTO dto = usersMapper.toResponse(u);
            dto.setCampusIds(usersService.getCampusIdsForUser(u.getId()));
            return dto;
        }).toList();
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuário")
    public ResponseEntity<UsuariosDTO> update(
            @Parameter(description = "ID do usuário") @PathVariable Integer id,
            @Valid @RequestBody UpdateUserRequestDTO dto) {
        Users existing = usersService.findById(id);
        usersMapper.applyUpdate(existing, dto);
        Users updated = usersService.updateWithCampuses(id, existing, dto.getCampusIds());
        UsuariosDTO response = usersMapper.toResponse(updated);
        response.setCampusIds(usersService.getCampusIdsForUser(id));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar usuário (soft-delete)")
    public ResponseEntity<Void> deactivate(
            @Parameter(description = "ID do usuário") @PathVariable Integer id) {
        usersService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/device-token")
    @Operation(summary = "Registrar device token para notificações", description = "Salva o Player ID do OneSignal gerado pelo Flutter. "
            +
            "Deve ser chamado logo após o login. " +
            "Envie o token como texto puro no body. " +
            "Body vazio remove o token (desabilita notificações).")
    public ResponseEntity<Void> registerDeviceToken(
            @Parameter(description = "ID do usuário") @PathVariable Integer id,
            @RequestBody(required = false) String deviceToken) {
        usersService.registerDeviceToken(id, deviceToken);
        return ResponseEntity.noContent().build();
    }
}
