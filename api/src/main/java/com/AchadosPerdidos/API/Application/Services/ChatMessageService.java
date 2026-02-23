package com.AchadosPerdidos.API.Application.Services;

import com.AchadosPerdidos.API.Application.Interfaces.IChatMessageService;
import com.AchadosPerdidos.API.Domain.Entity.Chat_Message.ChatMessage;
import com.AchadosPerdidos.API.Domain.Enum.Status_Message;
import com.AchadosPerdidos.API.Domain.Repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatMessageService implements IChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    @Override
    @Transactional
    public ChatMessage send(String chatId, Integer senderId, String content) {
        ChatMessage message = new ChatMessage();
        message.setChatId(chatId);
        message.setSenderId(senderId);
        message.setContent(content);
        message.setStatus(Status_Message.SENT);
        message.setCreatedAt(LocalDateTime.now());

        return chatMessageRepository.save(message);
    }

    @Override
    public List<ChatMessage> findByChat(String chatId) {
        return chatMessageRepository.findByChatIdOrderByCreatedAtAsc(chatId);
    }

    @Override
    public List<ChatMessage> findByChatAndStatus(String chatId, Status_Message status) {
        return chatMessageRepository.findByChatIdAndStatus(chatId, status);
    }

    @Override
    public long countUnread(String chatId, Integer senderId) {
        return chatMessageRepository.countByChatIdAndSenderIdAndStatus(chatId, senderId, Status_Message.SENT);
    }

    @Override
    @Transactional
    public void markAllAsRead(String chatId) {
        List<ChatMessage> messages = chatMessageRepository.findByChatIdAndStatus(chatId, Status_Message.SENT);
        for (ChatMessage msg : messages) {
            msg.setStatus(Status_Message.READ);
        }
        chatMessageRepository.saveAll(messages);
    }
}
