package com.AchadosPerdidos.API.Application.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Chat;

import java.util.List;

public interface IChatService {

    Chat openChat(Integer itemId);

    Chat findById(Integer id);

    Chat findByItem(Integer itemId);

    List<Chat> findAll();

    void closeChat(Integer chatId);
}
