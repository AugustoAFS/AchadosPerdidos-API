package com.AchadosPerdidos.API.Application.DTOs.Request.Chat;

import lombok.Data;

/**
 * Payload enviado pelo cliente via STOMP para /app/chat/{chatId}/typing
 * Evento efêmero — NÃO é persistido no banco.
 */
@Data
public class WsTypingEventDTO {

    private Integer senderId;

    /** true = está digitando, false = parou de digitar */
    private boolean typing;
}
