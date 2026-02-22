package com.AchadosPerdidos.API.Application.DTOs.Request.Campus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCampusRequestDTO {

    @NotBlank(message = "Nome do campus é obrigatório")
    private String name;

    @NotNull(message = "Instituição é obrigatória")
    private Integer institutionId;
}
