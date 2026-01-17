package com.AchadosPerdidos.API.Application.Services;

import com.AchadosPerdidos.API.Application.DTOs.Usuario.UsuariosDTO;
import com.AchadosPerdidos.API.Application.DTOs.Item.ItemDTO;
import com.AchadosPerdidos.API.Application.Services.Interfaces.INotificationService;
import com.AchadosPerdidos.API.Application.Services.Interfaces.IUsuariosService;
import com.AchadosPerdidos.API.Application.Services.Interfaces.IItensService;
import com.AchadosPerdidos.API.Application.Services.Interfaces.IChatService;
import com.AchadosPerdidos.API.Application.Config.OneSignalConfig;
import com.AchadosPerdidos.API.Domain.Entity.Chat_Message.ChatMessage;
import com.AchadosPerdidos.API.Domain.Enum.Tipo_Menssagem;
import com.AchadosPerdidos.API.Domain.Enum.Status_Menssagem;
import com.AchadosPerdidos.API.Domain.Enum.Status_Item;
import com.AchadosPerdidos.API.Domain.Repository.UsuariosRepository;
import com.AchadosPerdidos.API.Domain.Repository.ItensRepository;
import com.AchadosPerdidos.API.Domain.Repository.DeviceTokenRepository;
import com.AchadosPerdidos.API.Domain.Entity.DeviceToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Serviço de notificações automáticas para o sistema de achados e perdidos
 * Implementa as funcionalidades mencionadas no TCC para notificações automáticas
 */
@Service
public class NotificationService implements INotificationService {

    @Autowired
    private IUsuariosService usuariosService;

    @Autowired
    private IItensService itensService;

    @Autowired
    private IChatService chatService;

    @Autowired
    private UsuariosRepository usuariosRepository;

    @Autowired
    private ItensRepository itensRepository;

    @Autowired
    private DeviceTokenRepository deviceTokenRepository;

    @Autowired(required = false)
    private OneSignalConfig oneSignalConfig;

    /**
     * Notifica quando um item é encontrado e registrado no sistema
     * @param itemId ID do item encontrado
     * @param finderId ID do usuário que encontrou
     */
    @Override
    @Async
    public void sendItemFoundNotification(int itemId, int finderId) {
        try {
            // Busca o item e o usuário que encontrou
            ItemDTO item = itensService.getItemById(itemId);
            var finderList = usuariosService.getUsuarioById(finderId);
            
            if (item != null && finderList != null && finderList.getUsuarios() != null && !finderList.getUsuarios().isEmpty()) {
                UsuariosDTO finder = finderList.getUsuarios().get(0);
                
                // Busca o nome do campus do usuário que encontrou o item
                String campusNome = usuariosRepository.getCampusNomeAtivoByUsuarioId(finderId);
                if (campusNome == null || campusNome.trim().isEmpty()) {
                    campusNome = "Campus não informado";
                }
                
                // Cria mensagem de notificação
                String message = String.format(
                    "Novo item encontrado: %s. Local: %s. Encontrado por: %s",
                    item.getNome(),
                    campusNome,
                    finder.getNomeCompleto()
                );
                
                // Envia notificação para todos os usuários ativos
                sendNotificationToAllUsers(message, "ITEM_ENCONTRADO");
                
                System.out.println("Notificação enviada: Item encontrado - " + item.getNome());
            }
        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação de item encontrado: " + e.getMessage());
        }
    }

    /**
     * Notifica quando um item é reivindicado
     * @param itemId ID do item reivindicado
     * @param claimantId ID do usuário que reivindicou
     * @param ownerId ID do proprietário do item
     */
    @Override
    @Async
    public void sendItemClaimedNotification(int itemId, int claimantId, int ownerId) {
        try {
            ItemDTO item = itensService.getItemById(itemId);
            var claimantList = usuariosService.getUsuarioById(claimantId);
            var ownerList = usuariosService.getUsuarioById(ownerId);
            
            if (item != null && claimantList != null && claimantList.getUsuarios() != null && !claimantList.getUsuarios().isEmpty() 
                && ownerList != null && ownerList.getUsuarios() != null && !ownerList.getUsuarios().isEmpty()) {
                
                UsuariosDTO claimant = claimantList.getUsuarios().get(0);
                
                // Notifica o proprietário
                String ownerMessage = String.format(
                    "Seu item '%s' foi reivindicado por %s. Verifique a reivindicação.",
                    item.getNome(),
                    claimant.getNomeCompleto()
                );
                
                sendNotificationToUser(ownerId, ownerMessage, "ITEM_REIVINDICADO");
                
                // Notifica o reivindicador
                String claimantMessage = String.format(
                    "Sua reivindicação do item '%s' foi registrada. Aguarde confirmação do proprietário.",
                    item.getNome()
                );
                
                sendNotificationToUser(claimantId, claimantMessage, "REIVINDICACAO_REGISTRADA");
                
                System.out.println("Notificações enviadas: Item reivindicado - " + item.getNome());
            }
        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação de item reivindicado: " + e.getMessage());
        }
    }

    /**
     * Notifica quando um item é devolvido
     * @param itemId ID do item devolvido
     * @param ownerId ID do proprietário
     * @param finderId ID do usuário que encontrou
     */
    @Override
    @Async
    public void sendItemReturnedNotification(int itemId, int ownerId, int finderId) {
        try {
            ItemDTO item = itensService.getItemById(itemId);
            var ownerList = usuariosService.getUsuarioById(ownerId);
            var finderList = usuariosService.getUsuarioById(finderId);
            
            if (item != null && ownerList != null && ownerList.getUsuarios() != null && !ownerList.getUsuarios().isEmpty()
                && finderList != null && finderList.getUsuarios() != null && !finderList.getUsuarios().isEmpty()) {
                
                // Notifica o proprietário
                String ownerMessage = String.format(
                    "Seu item '%s' foi devolvido com sucesso! Obrigado por usar o sistema de achados e perdidos.",
                    item.getNome()
                );
                
                sendNotificationToUser(ownerId, ownerMessage, "ITEM_DEVOLVIDO");
                
                // Notifica quem encontrou
                String finderMessage = String.format(
                    "O item '%s' foi devolvido ao proprietário. Obrigado pela colaboração!",
                    item.getNome()
                );
                
                sendNotificationToUser(finderId, finderMessage, "ITEM_DEVOLVIDO");
                
                System.out.println("Notificações enviadas: Item devolvido - " + item.getNome());
            }
        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação de item devolvido: " + e.getMessage());
        }
    }

    /**
     * Notifica sobre itens próximos do prazo de doação (30 dias)
     * Executado diariamente às 9:00
     */
    @Override
    @Scheduled(cron = "0 0 9 * * ?")
    public void sendDonationDeadlineWarning() {
        try {
            // Busca itens ativos e filtra os que estão próximos do prazo (25+ dias desde a criação)
            LocalDateTime deadlineDate = LocalDateTime.now().minus(25, ChronoUnit.DAYS);
            List<com.AchadosPerdidos.API.Domain.Entity.Itens> allActiveItems = itensRepository.findActive();
            
            // Filtra itens criados há 25+ dias e que ainda não foram doados
            List<com.AchadosPerdidos.API.Domain.Entity.Itens> itemsNearDeadline = allActiveItems.stream()
                .filter(item -> item.getDtaCriacao() != null && item.getDtaCriacao().isBefore(deadlineDate))
                .collect(Collectors.toList());
            
            for (com.AchadosPerdidos.API.Domain.Entity.Itens item : itemsNearDeadline) {
                String message = String.format(
                    "ATENÇÃO: O item '%s' está próximo do prazo de doação (30 dias). " +
                    "Se não for reivindicado em breve, será destinado à doação.",
                    item.getNome()
                );
                
                Integer reporterId = item.getUsuario_relator_id() != null ? item.getUsuario_relator_id().getId() : null;
                if (reporterId != null) {
                    sendNotificationToUser(reporterId, message, "PRAZO_DOACAO");
                }
                
                System.out.println("Notificação de prazo enviada para item: " + item.getNome());
            }
        } catch (Exception e) {
            System.err.println("Erro ao enviar notificações de prazo: " + e.getMessage());
        }
    }

    /**
     * Marca itens como "Doado" após 30 dias sem reivindicação
     * Executado diariamente às 10:00
     */
    @Scheduled(cron = "0 0 10 * * ?")
    public void markItemsAsDonated() {
        try {
            // Busca itens ativos que foram criados há 30+ dias e ainda não foram doados
            LocalDateTime expiredDate = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
            List<com.AchadosPerdidos.API.Domain.Entity.Itens> allActiveItems = itensRepository.findActive();
            
            // Filtra itens criados há 30+ dias e que ainda não foram doados
            List<com.AchadosPerdidos.API.Domain.Entity.Itens> expiredItems = allActiveItems.stream()
                .filter(item -> item.getDtaCriacao() != null && item.getDtaCriacao().isBefore(expiredDate))
                .collect(Collectors.toList());
            
            for (com.AchadosPerdidos.API.Domain.Entity.Itens item : expiredItems) {
                try {
                    item.setFlgInativo(true);
                    item.setDtaRemocao(LocalDateTime.now());
                    item.setStatus_item(Status_Item.CANCELADO);
                    itensRepository.save(item);
                } catch (Exception e) {
                    System.err.println("Erro ao atualizar item como doado: " + e.getMessage());
                    continue;
                }
                
                // Notifica o usuário que encontrou
                String message = String.format(
                    "O item '%s' foi destinado à doação após 30 dias sem reivindicação, " +
                    "conforme política do sistema.",
                    item.getNome()
                );
                
                Integer reporterId = item.getUsuario_relator_id() != null ? item.getUsuario_relator_id().getId() : null;
                if (reporterId != null) {
                    sendNotificationToUser(reporterId, message, "ITEM_DOADO");
                }
                
                System.out.println("Item marcado como doado: " + item.getNome());
            }
        } catch (Exception e) {
            System.err.println("Erro ao marcar itens como doados: " + e.getMessage());
        }
    }

    /**
     * Envia notificação para um usuário específico
     */
    private void sendNotificationToUser(int userId, String message, String type) {
        try {
            // Cria mensagem de chat para notificação
            ChatMessage notification = new ChatMessage(
                "system",
                String.valueOf(userId),
                "system",
                message,
                Tipo_Menssagem.SYSTEM,
                Status_Menssagem.ENVIADA
            );
            
            // Salva no MongoDB
            chatService.saveMessage(notification);
            
            // Envia notificação push para dispositivos móveis (se configurado)
            if (oneSignalConfig != null && oneSignalConfig.isEnabled()) {
                try {
                    List<String> deviceTokens = getDeviceTokensForUser(userId);
                    if (deviceTokens != null && !deviceTokens.isEmpty()) {
                        oneSignalConfig.sendPushNotificationToMultiple(
                            deviceTokens,
                            "Achados e Perdidos",
                            message,
                            Map.of("type", type, "userId", String.valueOf(userId))
                        );
                    }
                } catch (Exception e) {
                    // Não interrompe o fluxo se push notification falhar
                    System.err.println("Erro ao enviar notificação push: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação para usuário " + userId + ": " + e.getMessage());
        }
    }

    /**
     * Envia notificação para todos os usuários ativos
     */
    private void sendNotificationToAllUsers(String message, String type) {
        try {
            // Busca todos os usuários ativos
            List<com.AchadosPerdidos.API.Domain.Entity.Usuario> activeUsers = usuariosRepository.findActive();
            
            // Envia notificação para cada usuário ativo
            for (com.AchadosPerdidos.API.Domain.Entity.Usuario usuario : activeUsers) {
                sendNotificationToUser(usuario.getId(), message, type);
            }
            
            System.out.println("Notificação geral enviada para " + activeUsers.size() + " usuários ativos: " + message);
            
        } catch (Exception e) {
            System.err.println("Erro ao enviar notificação geral: " + e.getMessage());
        }
    }

    /**
     * Busca os tokens de dispositivos de um usuário para envio de push notifications
     * @param userId ID do usuário
     * @return Lista de tokens de dispositivos ativos do usuário
     */
    private List<String> getDeviceTokensForUser(int userId) {
        List<DeviceToken> deviceTokens = deviceTokenRepository.findActiveTokensByUsuarioId(userId);
        return deviceTokens.stream()
            .map(DeviceToken::getToken)
            .collect(Collectors.toList());
    }
}
