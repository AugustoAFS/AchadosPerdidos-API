package com.AchadosPerdidos.API.Infrastruture.MongoDB.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Chat_Message.ChatMessage;
import com.AchadosPerdidos.API.Domain.Enum.Tipo_Menssagem;
import com.AchadosPerdidos.API.Domain.Enum.Status_Menssagem;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Interface para consultas MongoDB da coleção ChatMessage
 * Define os contratos para todas as queries relacionadas ao chat
 */
public interface IChatQuery {
    
    // Busca mensagens por chat
    List<ChatMessage> findMessagesByChatId(String chatId);
    
    // Busca mensagens entre usuários
    List<ChatMessage> findMessagesBetweenUsers(String userId1, String userId2);
    
    // Busca mensagens por período
    List<ChatMessage> findMessagesByPeriod(String chatId, LocalDateTime startTime, LocalDateTime endTime);
    
    // Busca mensagens não lidas
    List<ChatMessage> findUnreadMessages(String receiverId);
    
    // Busca últimas N mensagens
    List<ChatMessage> findRecentMessages(String chatId, int limit);
    
    // Conta mensagens por chat
    long countMessagesByChat(String chatId);
    
    // Busca mensagens por tipo
    List<ChatMessage> findMessagesByType(String chatId, Tipo_Menssagem type);
    
    // Busca mensagens por status
    List<ChatMessage> findMessagesByStatus(String receiverId, Status_Menssagem status);
}
