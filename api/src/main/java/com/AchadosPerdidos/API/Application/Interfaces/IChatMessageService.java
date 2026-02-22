package com.AchadosPerdidos.API.Application.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Chat_Message.ChatMessage;
import com.AchadosPerdidos.API.Domain.Enum.Status_Message;

import java.util.List;

public interface IChatMessageService {

    ChatMessage send(String chatId, Integer senderId, String content);

    List<ChatMessage> findByChat(String chatId);

    List<ChatMessage> findByChatAndStatus(String chatId, Status_Message status);

    long countUnread(String chatId, Integer senderId);

    void markAllAsRead(String chatId);
}
