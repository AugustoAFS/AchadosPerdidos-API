package com.AchadosPerdidos.API.Application.DTOs.Response.Campus;

import lombok.Data;

@Data
public class CampusResponseDTO {

    private Integer id;
    private String name;
    private Integer institutionId;
    private String institutionName;
}
