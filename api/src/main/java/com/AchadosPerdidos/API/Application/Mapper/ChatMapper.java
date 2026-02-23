package com.AchadosPerdidos.API.Application.Mapper;

import com.AchadosPerdidos.API.Application.DTOs.Response.Chat.ChatMessageResponseDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Chat.ChatResponseDTO;
import com.AchadosPerdidos.API.Domain.Entity.Chat;
import com.AchadosPerdidos.API.Domain.Entity.Chat_Message.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChatMapper {

    public ChatResponseDTO toResponse(Chat chat) {
        if (chat == null)
            return null;

        ChatResponseDTO dto = new ChatResponseDTO();
        dto.setId(chat.getId());
        dto.setItemId(chat.getItemId());
        return dto;
    }

    public List<ChatResponseDTO> toResponseList(List<Chat> chats) {
        if (chats == null)
            return List.of();
        return chats.stream().map(this::toResponse).toList();
    }

    public ChatMessageResponseDTO toMessageResponse(ChatMessage message) {
        if (message == null)
            return null;

        ChatMessageResponseDTO dto = new ChatMessageResponseDTO();
        dto.setId(message.getId());
        dto.setChatId(message.getChatId());
        dto.setSenderId(message.getSenderId());
        dto.setContent(message.getContent());
        dto.setCreatedAt(message.getCreatedAt());
        dto.setStatus(message.getStatus());
        return dto;
    }

    public List<ChatMessageResponseDTO> toMessageResponseList(List<ChatMessage> messages) {
        if (messages == null)
            return List.of();
        return messages.stream().map(this::toMessageResponse).toList();
    }
}
