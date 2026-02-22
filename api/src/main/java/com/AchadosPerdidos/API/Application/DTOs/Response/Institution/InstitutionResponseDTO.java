package com.AchadosPerdidos.API.Application.DTOs.Response.Institution;

import lombok.Data;

@Data
public class InstitutionResponseDTO {

    private Integer id;
    private String name;
    private String prefix;
    private String cnpj;
}
