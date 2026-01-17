package com.AchadosPerdidos.API.Infrastruture.MongoDB;

import com.AchadosPerdidos.API.Domain.Entity.Chat_Message.ChatMessage;
import com.AchadosPerdidos.API.Infrastruture.MongoDB.Interfaces.IChatQuery;
import com.AchadosPerdidos.API.Domain.Enum.Tipo_Menssagem;
import com.AchadosPerdidos.API.Domain.Enum.Status_Menssagem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Queries MongoDB para ChatMessage
 * Gerencia operações de chat em tempo real
 * Usa MongoDB para melhor performance em mensagens instantâneas
 */
@Repository
public class ChatQuery implements IChatQuery {

    @Autowired
    private MongoTemplate mongoTemplate;

    private static final String COLLECTION_NAME = "chat_messages";

    // ==================== CRUD BÁSICO ====================

    public List<ChatMessage> findAll() {
        return mongoTemplate.findAll(ChatMessage.class, COLLECTION_NAME);
    }

    public ChatMessage findById(String id) {
        return mongoTemplate.findById(id, ChatMessage.class, COLLECTION_NAME);
    }

    public ChatMessage insert(ChatMessage chatMessage) {
        if (chatMessage.getData_Hora_Menssagem() == null) {
            chatMessage.setData_Hora_Menssagem(LocalDateTime.now());
        }
        return mongoTemplate.insert(chatMessage, COLLECTION_NAME);
    }

    public ChatMessage update(ChatMessage chatMessage) {
        return mongoTemplate.save(chatMessage, COLLECTION_NAME);
    }

    public boolean deleteById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        return mongoTemplate.remove(query, ChatMessage.class, COLLECTION_NAME).getDeletedCount() > 0;
    }

    // ==================== BUSCAS POR CHAT ====================

    public List<ChatMessage> findByChatId(String chatId) {
        Query query = new Query(Criteria.where("id_Chat").is(chatId));
        return mongoTemplate.find(query, ChatMessage.class, COLLECTION_NAME);
    }

    public List<ChatMessage> findByChatIdOrderByDataHora(String chatId) {
        Query query = new Query(Criteria.where("id_Chat").is(chatId))
                .with(org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.ASC, "data_Hora_Menssagem"));
        return mongoTemplate.find(query, ChatMessage.class, COLLECTION_NAME);
    }

    @Override
    public List<ChatMessage> findMessagesByChatId(String chatId) {
        return findByChatIdOrderByDataHora(chatId);
    }

    // ==================== BUSCAS POR USUÁRIO ====================

    public List<ChatMessage> findByRemetenteId(String remetenteId) {
        Query query = new Query(Criteria.where("id_Usuario_Remetente").is(remetenteId));
        return mongoTemplate.find(query, ChatMessage.class, COLLECTION_NAME);
    }

    public List<ChatMessage> findByDestinoId(String destinoId) {
        Query query = new Query(Criteria.where("id_Usuario_Destino").is(destinoId));
        return mongoTemplate.find(query, ChatMessage.class, COLLECTION_NAME);
    }

    // ==================== BUSCAS POR CONVERSA ====================

    public List<ChatMessage> findConversation(String usuario1Id, String usuario2Id) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("id_Usuario_Remetente").is(usuario1Id)
                        .and("id_Usuario_Destino").is(usuario2Id),
                Criteria.where("id_Usuario_Remetente").is(usuario2Id)
                        .and("id_Usuario_Destino").is(usuario1Id)
        );
        Query query = new Query(criteria);
        return mongoTemplate.find(query, ChatMessage.class, COLLECTION_NAME);
    }

    public List<ChatMessage> findConversationOrderByDataHora(String usuario1Id, String usuario2Id) {
        Criteria criteria = new Criteria().orOperator(
                Criteria.where("id_Usuario_Remetente").is(usuario1Id)
                        .and("id_Usuario_Destino").is(usuario2Id),
                Criteria.where("id_Usuario_Remetente").is(usuario2Id)
                        .and("id_Usuario_Destino").is(usuario1Id)
        );
        Query query = new Query(criteria)
                .with(org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.ASC, "data_Hora_Menssagem"));
        return mongoTemplate.find(query, ChatMessage.class, COLLECTION_NAME);
    }

    @Override
    public List<ChatMessage> findMessagesBetweenUsers(String userId1, String userId2) {
        return findConversationOrderByDataHora(userId1, userId2);
    }

    // ==================== BUSCAS POR STATUS ====================

    public List<ChatMessage> findByStatus(Status_Menssagem status) {
        Query query = new Query(Criteria.where("status").is(status));
        return mongoTemplate.find(query, ChatMessage.class, COLLECTION_NAME);
    }

    @Override
    public List<ChatMessage> findUnreadMessages(String receiverId) {
        Query query = new Query(
                Criteria.where("id_Usuario_Destino").is(receiverId)
                        .and("status").ne(Status_Menssagem.LIDA)
        );
        return mongoTemplate.find(query, ChatMessage.class, COLLECTION_NAME);
    }


    // ==================== OPERAÇÕES DE STATUS ====================

    public boolean markAsRead(String messageId) {
        Query query = new Query(Criteria.where("_id").is(messageId));
        Update update = new Update().set("status", Status_Menssagem.LIDA);
        return mongoTemplate.updateFirst(query, update, ChatMessage.class, COLLECTION_NAME)
                .getModifiedCount() > 0;
    }

    public boolean markAsDelivered(String messageId) {
        Query query = new Query(Criteria.where("_id").is(messageId));
        Update update = new Update().set("status", Status_Menssagem.RECEBIDA);
        return mongoTemplate.updateFirst(query, update, ChatMessage.class, COLLECTION_NAME)
                .getModifiedCount() > 0;
    }

    public int countUnreadMessages(String destinoId) {
        Query query = new Query(
                Criteria.where("id_Usuario_Destino").is(destinoId)
                        .and("status").ne(Status_Menssagem.LIDA)
        );
        return (int) mongoTemplate.count(query, ChatMessage.class, COLLECTION_NAME);
    }

    // ==================== BUSCAS POR DATA ====================

    public List<ChatMessage> findMessagesBetweenDates(String chatId, LocalDateTime startDate, LocalDateTime endDate) {
        Query query = new Query(
                Criteria.where("id_Chat").is(chatId)
                        .and("data_Hora_Menssagem").gte(startDate).lte(endDate)
        ).with(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.ASC, "data_Hora_Menssagem"));
        return mongoTemplate.find(query, ChatMessage.class, COLLECTION_NAME);
    }

    @Override
    public List<ChatMessage> findMessagesByPeriod(String chatId, LocalDateTime startTime, LocalDateTime endTime) {
        return findMessagesBetweenDates(chatId, startTime, endTime);
    }

    public ChatMessage findLastMessage(String chatId) {
        Query query = new Query(Criteria.where("id_Chat").is(chatId))
                .with(org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "data_Hora_Menssagem"))
                .limit(1);
        return mongoTemplate.findOne(query, ChatMessage.class, COLLECTION_NAME);
    }

    @Override
    public List<ChatMessage> findRecentMessages(String chatId, int limit) {
        Query query = new Query(Criteria.where("id_Chat").is(chatId))
                .with(org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Direction.DESC, "data_Hora_Menssagem"))
                .limit(limit);
        return mongoTemplate.find(query, ChatMessage.class, COLLECTION_NAME);
    }

    @Override
    public long countMessagesByChat(String chatId) {
        Query query = new Query(Criteria.where("id_Chat").is(chatId));
        return mongoTemplate.count(query, ChatMessage.class, COLLECTION_NAME);
    }

    @Override
    public List<ChatMessage> findMessagesByType(String chatId, Tipo_Menssagem type) {
        Query query = new Query(
                Criteria.where("id_Chat").is(chatId)
                        .and("tipo").is(type)
        ).with(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.ASC, "data_Hora_Menssagem"));
        return mongoTemplate.find(query, ChatMessage.class, COLLECTION_NAME);
    }

    @Override
    public List<ChatMessage> findMessagesByStatus(String receiverId, Status_Menssagem status) {
        Query query = new Query(
                Criteria.where("id_Usuario_Destino").is(receiverId)
                        .and("status").is(status)
        );
        return mongoTemplate.find(query, ChatMessage.class, COLLECTION_NAME);
    }
}
