package com.AchadosPerdidos.API.Application.DTOs.Request.Institution;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateInstitutionRequestDTO {

    @NotBlank(message = "Nome da instituição é obrigatório")
    private String name;

    @NotBlank(message = "Prefixo é obrigatório")
    private String prefix;

    @NotBlank(message = "CNPJ é obrigatório")
    private String cnpj;
}
