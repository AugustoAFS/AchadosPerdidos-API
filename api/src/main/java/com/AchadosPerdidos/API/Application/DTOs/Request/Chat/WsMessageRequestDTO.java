package com.AchadosPerdidos.API.Application.DTOs.Request.Chat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Payload enviado pelo cliente via STOMP para /app/chat/{chatId}/send
 */
@Data
public class WsMessageRequestDTO {

    @NotNull(message = "ID do remetente é obrigatório")
    private Integer senderId;

    @NotBlank(message = "Conteúdo da mensagem é obrigatório")
    private String content;

    /**
     * ID do usuário destinatário — necessário para disparar a push notification.
     * O Flutter deve preencher com o ID do outro participante do chat.
     */
    private Integer receiverId;
}
