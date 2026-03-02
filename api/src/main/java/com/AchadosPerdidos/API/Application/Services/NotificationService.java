package com.AchadosPerdidos.API.Application.Services;

import com.AchadosPerdidos.API.Application.Interfaces.Notification.IPushNotificationService;
import com.AchadosPerdidos.API.Domain.Entity.Item;
import com.AchadosPerdidos.API.Domain.Enum.Type_Item;
import com.AchadosPerdidos.API.Domain.Repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Serviço de notificações de negócio.
 * Centraliza todos os cenários de push notification do sistema.
 * Cada método é @Async para não bloquear a thread principal da requisição.
 *
 * Cenários implementados:
 * - Novo item publicado (FIND ou LOST) → notifica usuários do mesmo campus
 * - Nova mensagem de chat recebida → notifica o destinatário
 */
@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private IPushNotificationService pushService;

    @Autowired
    private UsersRepository usersRepository;

    // ─────────────────────────────────────────────────────────────────────────
    // Novo Item
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Notifica todos os usuários ativos no campus do item sobre um novo achado ou
     * perda.
     * O autor do item é excluído da lista (não faz sentido notificar quem
     * publicou).
     *
     * @param item Item recém-criado
     */
    @Async
    public void notifyNewItem(Item item) {
        if (!pushService.isAvailable()) {
            log.debug("Push desabilitado — notifyNewItem ignorado para itemId={}", item.getId());
            return;
        }

        List<String> tokens = usersRepository.findDeviceTokensByCampusId(
                item.getCampusId(),
                item.getAuthorUserId() != null ? item.getAuthorUserId() : -1);

        if (tokens.isEmpty()) {
            log.debug("Nenhum token encontrado para campusId={} — sem push", item.getCampusId());
            return;
        }

        boolean isFind = item.getTypeItem() == Type_Item.FIND;
        String title = isFind ? "📦 Item achado no campus!" : "🔍 Alguém perdeu um item!";
        String message = isFind
                ? "Um item foi encontrado: " + item.getTitle() + ". Reconhece?"
                : item.getTitle() + " foi reportado como perdido. Já viu?";

        Map<String, String> data = Map.of(
                "type", "NEW_ITEM",
                "itemId", String.valueOf(item.getId()),
                "itemType", item.getTypeItem().name());

        boolean sent = pushService.sendToMultipleDevices(tokens, title, message, data);
        log.info("Push notifyNewItem: itemId={} campus={} tokens={} enviado={}",
                item.getId(), item.getCampusId(), tokens.size(), sent);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Nova Mensagem de Chat
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Notifica o destinatário de uma mensagem de chat.
     * Só envia se o destinatário tiver device token registrado.
     *
     * @param chatId     ID do chat (MongoDB)
     * @param senderId   ID do usuário que enviou
     * @param receiverId ID do usuário que deve receber a notificação
     * @param content    Conteúdo da mensagem (truncado para preview)
     */
    @Async
    public void notifyNewChatMessage(String chatId, Integer senderId, Integer receiverId, String content) {
        if (!pushService.isAvailable()) {
            log.debug("Push desabilitado — notifyNewChatMessage ignorado");
            return;
        }

        Optional<String> tokenOpt = usersRepository.findDeviceTokenByUserId(receiverId);
        if (tokenOpt.isEmpty()) {
            log.debug("Usuário receiverId={} sem device token — sem push", receiverId);
            return;
        }

        String preview = content != null && content.length() > 60
                ? content.substring(0, 60) + "..."
                : content;
        String title = "💬 Nova mensagem";
        String message = preview;

        Map<String, String> data = Map.of(
                "type", "CHAT_MESSAGE",
                "chatId", chatId,
                "senderId", String.valueOf(senderId));

        boolean sent = pushService.sendToDevice(tokenOpt.get(), title, message, data);
        log.info("Push notifyNewChatMessage: chatId={} receiver={} enviado={}", chatId, receiverId, sent);
    }
}
