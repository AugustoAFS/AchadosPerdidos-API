package com.AchadosPerdidos.API.Application.Services;

import com.AchadosPerdidos.API.Application.Interfaces.IChatService;
import com.AchadosPerdidos.API.Domain.Entity.Chat;
import com.AchadosPerdidos.API.Domain.Repository.ChatRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public ChatService(ChatRepository repository) {
        super(repository);
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
        return repository.findByActiveTrue();
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
