package com.AchadosPerdidos.API.Application.DTOs.Request.Item;

import com.AchadosPerdidos.API.Domain.Enum.Type_Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateItemRequestDTO {

    @NotBlank(message = "Título é obrigatório")
    private String title;

    private String description;

    @NotNull(message = "Tipo do item é obrigatório")
    private Type_Item typeItem;

    private String meetingLocation;

    @NotNull(message = "Campus é obrigatório")
    private Integer campusId;

    private Integer categoryId;
}
