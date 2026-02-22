package com.AchadosPerdidos.API.Application.DTOs.Request.Item;

import com.AchadosPerdidos.API.Domain.Enum.Status_Item;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateItemStatusRequestDTO {

    @NotNull(message = "Status é obrigatório")
    private Status_Item status;

    /** Preenchido quando status = ENTREGUE. */
    private Integer receiverUserId;
}
