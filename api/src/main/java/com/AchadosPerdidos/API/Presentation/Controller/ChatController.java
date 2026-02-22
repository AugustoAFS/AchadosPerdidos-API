package com.AchadosPerdidos.API.Presentation.Controller;

import com.AchadosPerdidos.API.Application.DTOs.Request.Chat.SendMessageRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Chat.ChatMessageResponseDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Chat.ChatResponseDTO;
import com.AchadosPerdidos.API.Application.Interfaces.IChatMessageService;
import com.AchadosPerdidos.API.Application.Interfaces.IChatService;
import com.AchadosPerdidos.API.Application.Mapper.ChatMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@Tag(name = "Chat", description = "Gerenciamento de chats e mensagens entre usuários")
public class ChatController {

    @Autowired
    private IChatService chatService;

    @Autowired
    private IChatMessageService chatMessageService;

    @Autowired
    private ChatMapper chatMapper;

    @PostMapping("/item/{itemId}")
    @Operation(summary = "Abrir chat para um item", description = "Abre um chat associado ao item. Se já existir um chat ativo, retorna o existente")
    public ResponseEntity<ChatResponseDTO> openChat(
            @Parameter(description = "ID do item") @PathVariable Integer itemId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chatMapper.toResponse(chatService.openChat(itemId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar chat por ID")
    public ResponseEntity<ChatResponseDTO> findById(
            @Parameter(description = "ID do chat") @PathVariable Integer id) {
        return ResponseEntity.ok(chatMapper.toResponse(chatService.findById(id)));
    }

    @GetMapping("/item/{itemId}")
    @Operation(summary = "Buscar chat por item")
    public ResponseEntity<ChatResponseDTO> findByItem(
            @Parameter(description = "ID do item") @PathVariable Integer itemId) {
        return ResponseEntity.ok(chatMapper.toResponse(chatService.findByItem(itemId)));
    }

    @GetMapping
    @Operation(summary = "Listar todos os chats ativos")
    public ResponseEntity<List<ChatResponseDTO>> findAll() {
        return ResponseEntity.ok(chatMapper.toResponseList(chatService.findAll()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Encerrar chat (soft-delete)")
    public ResponseEntity<Void> closeChat(
            @Parameter(description = "ID do chat") @PathVariable Integer id) {
        chatService.closeChat(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{chatId}/messages")
    @Operation(summary = "Enviar mensagem", description = "Envia uma mensagem em um chat")
    public ResponseEntity<ChatMessageResponseDTO> sendMessage(
            @Parameter(description = "ID do chat") @PathVariable String chatId,
            @Valid @RequestBody SendMessageRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                chatMapper.toMessageResponse(
                        chatMessageService.send(chatId, dto.getSenderId(), dto.getContent())));
    }

    @GetMapping("/{chatId}/messages")
    @Operation(summary = "Listar mensagens do chat", description = "Retorna todas as mensagens ordenadas por data de criação")
    public ResponseEntity<List<ChatMessageResponseDTO>> getMessages(
            @Parameter(description = "ID do chat") @PathVariable String chatId) {
        return ResponseEntity.ok(chatMapper.toMessageResponseList(chatMessageService.findByChat(chatId)));
    }

    @PatchMapping("/{chatId}/messages/read")
    @Operation(summary = "Marcar mensagens como lidas", description = "Marca todas as mensagens não lidas do chat como LIDA")
    public ResponseEntity<Void> markAllAsRead(
            @Parameter(description = "ID do chat") @PathVariable String chatId) {
        chatMessageService.markAllAsRead(chatId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{chatId}/messages/unread")
    @Operation(summary = "Contar mensagens não lidas por remetente")
    public ResponseEntity<Long> countUnread(
            @Parameter(description = "ID do chat") @PathVariable String chatId,
            @Parameter(description = "ID do remetente") @RequestParam Integer senderId) {
        return ResponseEntity.ok(chatMessageService.countUnread(chatId, senderId));
    }
}
