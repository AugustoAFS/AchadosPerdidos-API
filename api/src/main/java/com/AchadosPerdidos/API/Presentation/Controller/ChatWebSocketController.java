package com.AchadosPerdidos.API.Presentation.Controller;

import com.AchadosPerdidos.API.Application.DTOs.Request.Chat.WsMessageRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Request.Chat.WsTypingEventDTO;
import com.AchadosPerdidos.API.Application.DTOs.Request.Chat.WsUserStatusDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Chat.ChatMessageResponseDTO;
import com.AchadosPerdidos.API.Application.Interfaces.IChatMessageService;
import com.AchadosPerdidos.API.Application.Mapper.ChatMapper;
import com.AchadosPerdidos.API.Application.Services.NotificationService;
import com.AchadosPerdidos.API.Application.Services.PresenceService;
import com.AchadosPerdidos.API.Domain.Entity.Chat_Message.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.util.Objects;

/**
 * Controller WebSocket — processa eventos STOMP em tempo real.
 *
 * Fluxo do cliente Flutter:
 * 1. Conectar: WS /ws com header "Authorization: Bearer <token>"
 * 2. Enviar mensagem: SEND /app/chat/{chatId}/send → publica em
 * /topic/chat/{chatId}/messages
 * 3. Indicar digitando: SEND /app/chat/{chatId}/typing → publica em
 * /topic/chat/{chatId}/typing
 * 4. Status online: SEND /app/user/status → publica em
 * /topic/users/{userId}/status
 * 5. Status offline ao desconectar: automático via SessionDisconnectEvent
 */
@Controller
public class ChatWebSocketController {

    private static final Logger log = LoggerFactory.getLogger(ChatWebSocketController.class);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private IChatMessageService chatMessageService;

    @Autowired
    private ChatMapper chatMapper;

    @Autowired
    private PresenceService presenceService;

    @Autowired
    private NotificationService notificationService;

    // ─────────────────────────────────────────────────────────────────────────
    // Mensagens instantâneas
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Cliente envia: SEND /app/chat/{chatId}/send
     * Servidor publica em: /topic/chat/{chatId}/messages
     *
     * A mensagem é persistida no MongoDB pelo ChatMessageService.
     */
    @MessageMapping("/chat/{chatId}/send")
    public void sendMessage(
            @DestinationVariable String chatId,
            @Payload WsMessageRequestDTO payload,
            SimpMessageHeaderAccessor headerAccessor) {

        log.info("WS MSG: chatId={} senderId={}", chatId, payload.getSenderId());

        ChatMessage saved = chatMessageService.send(chatId, payload.getSenderId(), payload.getContent());
        ChatMessageResponseDTO response = chatMapper.toMessageResponse(saved);
        Objects.requireNonNull(response, "Falha ao mapear mensagem");

        // Publica para todos os clientes subscritos no chat
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/messages", (Object) response);

        // Push notification para o destinatário (assíncrono — não bloqueia a entrega
        // WS)
        if (payload.getReceiverId() != null) {
            notificationService.notifyNewChatMessage(
                    chatId, payload.getSenderId(), payload.getReceiverId(), payload.getContent());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Indicador "está digitando..."
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Cliente envia: SEND /app/chat/{chatId}/typing
     * Payload: { "senderId": 42, "typing": true }
     * Servidor publica em: /topic/chat/{chatId}/typing
     *
     * NÃO é persistido — é um evento efêmero.
     */
    @MessageMapping("/chat/{chatId}/typing")
    public void typingIndicator(
            @DestinationVariable String chatId,
            @Payload WsTypingEventDTO payload) {

        log.debug("WS TYPING: chatId={} userId={} typing={}", chatId, payload.getSenderId(), payload.isTyping());

        // Repassa o evento para todos os outros participantes do chat
        messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/typing", payload);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Presença: Online / Offline
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Cliente envia: SEND /app/user/status
     * Payload: { "userId": 42, "status": "ONLINE" }
     * Servidor publica em: /topic/users/{userId}/status
     *
     * NÃO é persistido — estado in-memory via PresenceService.
     */
    @MessageMapping("/user/status")
    public void userStatus(@Payload WsUserStatusDTO payload) {
        log.info("WS STATUS: userId={} status={}", payload.getUserId(), payload.getStatus());

        if ("ONLINE".equalsIgnoreCase(payload.getStatus())) {
            presenceService.setOnline(payload.getUserId());
        } else {
            presenceService.setOffline(payload.getUserId());
        }

        // Publica para quem estiver monitorando o status desse usuário
        messagingTemplate.convertAndSend("/topic/users/" + payload.getUserId() + "/status", payload);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Eventos de conexão / desconexão
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Disparado automaticamente quando um cliente conecta via WebSocket.
     * Loga a conexão para rastreabilidade.
     */
    @EventListener
    public void handleConnect(SessionConnectedEvent event) {
        Principal principal = event.getUser();
        if (principal != null) {
            log.info("WS CONNECTED: user={}", principal.getName());
        }
    }

    /**
     * Disparado automaticamente quando um cliente desconecta.
     * Marca o usuário como OFFLINE e notifica os interessados.
     *
     * O userId é armazenado como "credentials" no
     * UsernamePasswordAuthenticationToken
     * durante a autenticação JWT no WebSocketConfig.
     */
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        if (principal instanceof org.springframework.security.authentication.UsernamePasswordAuthenticationToken auth) {
            Object credentials = auth.getCredentials();
            if (credentials != null) {
                try {
                    Integer userId = Integer.parseInt(credentials.toString());
                    presenceService.setOffline(userId);

                    WsUserStatusDTO statusEvent = new WsUserStatusDTO();
                    statusEvent.setUserId(userId);
                    statusEvent.setStatus("OFFLINE");

                    messagingTemplate.convertAndSend("/topic/users/" + userId + "/status", statusEvent);
                    log.info("WS DISCONNECTED: userId={} marcado como OFFLINE", userId);
                } catch (NumberFormatException e) {
                    log.warn("WS DISCONNECTED: userId inválido nos credentials: {}", credentials);
                }
            }
        }
    }
}
