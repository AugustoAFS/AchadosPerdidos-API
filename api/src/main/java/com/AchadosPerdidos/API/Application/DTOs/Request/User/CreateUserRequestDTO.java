package com.AchadosPerdidos.API.Application.DTOs.Request.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CreateUserRequestDTO {

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    private String password;

    @Past(message = "Data de nascimento deve ser no passado")
    private Date birthDate;

    @NotNull(message = "Pelo menos um campus é obrigatório")
    private List<Integer> campusIds;

    private Integer roleId;
}
