package com.AchadosPerdidos.API.Domain.Repository;

import com.AchadosPerdidos.API.Domain.Entity.Chat_Message.ChatMessage;
import com.AchadosPerdidos.API.Domain.Enum.Status_Message;
import com.AchadosPerdidos.API.Domain.Interfaces.IChatMessageRepository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String>, IChatMessageRepository {

    @Override
    List<ChatMessage> findByChatIdOrderByCreatedAtAsc(String chatId);

    @Override
    List<ChatMessage> findByChatIdAndStatus(String chatId, Status_Message status);

    @Override
    long countByChatIdAndSenderIdAndStatus(String chatId, Integer senderId, Status_Message status);

    @Override
    List<ChatMessage> findBySenderId(Integer senderId);
}
