package com.AchadosPerdidos.API.Application.Services;

import com.AchadosPerdidos.API.Application.Interfaces.IChatService;
import com.AchadosPerdidos.API.Domain.Entity.Chat;
import com.AchadosPerdidos.API.Domain.Entity.Chat_Message.ChatMessage;
import com.AchadosPerdidos.API.Domain.Entity.Item;
import com.AchadosPerdidos.API.Domain.Repository.ChatRepository;
import com.AchadosPerdidos.API.Domain.Repository.ChatMessageRepository;
import com.AchadosPerdidos.API.Domain.Repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService extends BaseService<Chat, Integer, ChatRepository>
        implements IChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatMessageRepository chatMessageRepository;
    private final ItemRepository itemRepository;

    public ChatService(ChatRepository repository, ChatMessageRepository chatMessageRepository,
            ItemRepository itemRepository) {
        super(repository);
        this.chatMessageRepository = chatMessageRepository;
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional
    public Chat update(Integer id, Chat data) {
        throw new UnsupportedOperationException("Chat não suporta update genérico. Use openChat/closeChat.");
    }

    @Override
    @Transactional
    public void deactivate(Integer id) {
        closeChat(id);
    }

    @Override
    @Transactional
    public Chat openChat(Integer itemId) {
        return repository.findByItemIdAndActiveTrue(itemId)
                .orElseGet(() -> {
                    Chat chat = new Chat();
                    chat.setItemId(itemId);
                    Chat created = repository.save(chat);
                    log.info("Chat aberto para itemId={} | chatId={}", itemId, created.getId());
                    return created;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Chat findByItem(Integer itemId) {
        return repository.findByItemIdAndActiveTrue(itemId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Nenhum chat ativo para o item: " + itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Chat> findAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getCredentials() == null) {
            return List.of(); // Sem usuário logado = sem chats
        }

        Integer currentUserId = Integer.valueOf(auth.getCredentials().toString());

        // Todos os chats ativos
        List<Chat> allActiveChats = repository.findByActiveTrue();

        // Pega todos os IDs de chat que o usuário atual já mandou mensagem
        List<String> chatsWhereUserSentMessages = chatMessageRepository.findBySenderId(currentUserId).stream()
                .map(ChatMessage::getChatId)
                .distinct()
                .toList();

        // Filtra para manter somente se:
        // 1. O usuário é o DONO do item daquele chat
        // 2. OU o usuário é um REQUISITANTE (já mandou mensagem naquele chat)
        return allActiveChats.stream().filter(chat -> {
            Item item = itemRepository.findById(chat.getItemId()).orElse(null);
            boolean isOwner = (item != null && item.getAuthorUserId().equals(currentUserId));
            boolean isParticipant = chatsWhereUserSentMessages.contains(String.valueOf(chat.getId()));
            return isOwner || isParticipant;
        }).toList();
    }

    @Override
    @Transactional
    public void closeChat(Integer chatId) {
        Chat chat = findById(chatId);
        chat.setActive(false);
        chat.setDeletedAt(LocalDateTime.now());
        repository.save(chat);
        log.info("Chat encerrado id={}", chatId);
    }
}
