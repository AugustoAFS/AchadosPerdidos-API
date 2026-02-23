package com.AchadosPerdidos.API.Domain.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Chat;

import java.util.List;
import java.util.Optional;

public interface IChatRepository {

    Optional<Chat> findByItemIdAndActiveTrue(Integer itemId);

    List<Chat> findByItemIdAndActiveTrue_AllChats(Integer itemId);

    boolean existsByItemId(Integer itemId);
}
