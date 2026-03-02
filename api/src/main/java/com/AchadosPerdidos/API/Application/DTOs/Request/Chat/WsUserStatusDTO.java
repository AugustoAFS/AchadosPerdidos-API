package com.AchadosPerdidos.API.Application.DTOs.Request.Chat;

import lombok.Data;

/**
 * Payload enviado pelo cliente via STOMP para /app/user/status
 * Evento efêmero — NÃO é persistido no banco.
 */
@Data
public class WsUserStatusDTO {

    private Integer userId;

    /** ONLINE | OFFLINE */
    private String status;
}
