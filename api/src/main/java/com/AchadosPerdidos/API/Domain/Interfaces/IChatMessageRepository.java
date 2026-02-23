package com.AchadosPerdidos.API.Domain.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Chat_Message.ChatMessage;
import com.AchadosPerdidos.API.Domain.Enum.Status_Message;

import java.util.List;

public interface IChatMessageRepository {

    List<ChatMessage> findByChatIdOrderByCreatedAtAsc(String chatId);

    List<ChatMessage> findByChatIdAndStatus(String chatId, Status_Message status);

    long countByChatIdAndSenderIdAndStatus(String chatId, Integer senderId, Status_Message status);

    List<ChatMessage> findBySenderId(Integer senderId);
}
