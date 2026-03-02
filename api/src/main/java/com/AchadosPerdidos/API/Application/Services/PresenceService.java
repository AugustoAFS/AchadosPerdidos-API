package com.AchadosPerdidos.API.Application.Services;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Serviço in-memory de presença de usuários.
 * Rastreia quais userId estão atualmente conectados via WebSocket.
 *
 * Nota: em ambiente com múltiplas instâncias (cluster), substituir por Redis.
 */
@Service
public class PresenceService {

    private final Set<Integer> onlineUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void setOnline(Integer userId) {
        onlineUsers.add(userId);
    }

    public void setOffline(Integer userId) {
        onlineUsers.remove(userId);
    }

    public boolean isOnline(Integer userId) {
        return onlineUsers.contains(userId);
    }

    public Set<Integer> getOnlineUsers() {
        return Collections.unmodifiableSet(onlineUsers);
    }
}
