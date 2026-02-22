package com.AchadosPerdidos.API.Application.DTOs.Request.Category;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCategoryRequestDTO {

    @NotBlank(message = "Nome da categoria é obrigatório")
    private String name;
}
