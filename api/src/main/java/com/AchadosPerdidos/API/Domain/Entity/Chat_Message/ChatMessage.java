package com.AchadosPerdidos.API.Domain.Entity.Chat_Message;

import com.AchadosPerdidos.API.Domain.Enum.Status_Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "chat_messages")
public class ChatMessage {

    @Id
    private String id;
    @Field("Chat_Id")
    private String chatId;
    @Field("Sender_Id")
    private Integer senderId;
    private String content;
    @Field("Created_at")
    private LocalDateTime createdAt;
    private Status_Message status;

    // Digitando é um evento efêmero de WebSocket
}