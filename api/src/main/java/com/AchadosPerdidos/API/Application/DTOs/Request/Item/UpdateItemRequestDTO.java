package com.AchadosPerdidos.API.Application.DTOs.Request.Item;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateItemRequestDTO {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    private String description;

    private String meetingLocation;

    private Integer categoryId;
}
