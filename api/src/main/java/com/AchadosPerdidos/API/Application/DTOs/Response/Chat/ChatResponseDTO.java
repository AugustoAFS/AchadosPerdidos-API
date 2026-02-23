package com.AchadosPerdidos.API.Application.DTOs.Response.Chat;

import lombok.Data;

@Data
public class ChatResponseDTO {

    private Integer id;
    private Integer itemId;
    private String itemTitle;
    private long unreadCount;
}
