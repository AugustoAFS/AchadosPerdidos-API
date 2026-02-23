package com.AchadosPerdidos.API.Application.Mapper;

import com.AchadosPerdidos.API.Application.DTOs.Request.User.CreateUserRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Request.User.UpdateUserRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.User.UsuariosDTO;
import com.AchadosPerdidos.API.Domain.Entity.Users;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsersMapper {

    public UsuariosDTO toResponse(Users user) {
        if (user == null)
            return null;

        UsuariosDTO dto = new UsuariosDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setBirthDate(user.getBirthDate());
        dto.setRoleId(user.getRoleId());
        dto.setActive(user.isActive());
        return dto;
    }

    public List<UsuariosDTO> toResponseList(List<Users> users) {
        if (users == null)
            return List.of();
        return users.stream().map(this::toResponse).toList();
    }

    public Users fromCreate(CreateUserRequestDTO dto) {
        if (dto == null)
            return null;

        Users user = new Users();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(dto.getPassword());
        user.setRoleId(dto.getRoleId());
        user.setBirthDate(dto.getBirthDate());
        return user;
    }

    public void applyUpdate(Users user, UpdateUserRequestDTO dto) {
        if (user == null || dto == null)
            return;

        if (dto.getName() != null)
            user.setName(dto.getName());
        if (dto.getBirthDate() != null)
            user.setBirthDate(dto.getBirthDate());
    }
}
