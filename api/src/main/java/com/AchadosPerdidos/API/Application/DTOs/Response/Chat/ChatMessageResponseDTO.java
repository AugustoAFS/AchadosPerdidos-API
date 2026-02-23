package com.AchadosPerdidos.API.Application.DTOs.Response.Chat;

import com.AchadosPerdidos.API.Domain.Enum.Status_Message;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatMessageResponseDTO {

    private String id;
    private String chatId;
    private Integer senderId;
    private String senderName;
    private String content;
    private LocalDateTime createdAt;
    private Status_Message status;
}
