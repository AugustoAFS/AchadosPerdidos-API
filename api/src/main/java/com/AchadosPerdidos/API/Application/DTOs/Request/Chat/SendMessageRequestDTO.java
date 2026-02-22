package com.AchadosPerdidos.API.Application.DTOs.Request.Chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SendMessageRequestDTO {

    @NotNull(message = "ID do remetente é obrigatório")
    private Integer senderId;

    @NotBlank(message = "Conteúdo da mensagem é obrigatório")
    private String content;
}
