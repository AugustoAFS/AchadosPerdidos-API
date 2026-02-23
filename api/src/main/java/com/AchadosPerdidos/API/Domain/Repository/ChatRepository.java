package com.AchadosPerdidos.API.Domain.Repository;

import com.AchadosPerdidos.API.Domain.Entity.Chat;
import com.AchadosPerdidos.API.Domain.Interfaces.IChatRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ChatRepository extends BaseRepository<Chat, Integer>, IChatRepository {

    @Override
    Optional<Chat> findByItemIdAndActiveTrue(Integer itemId);

    @Query("SELECT c FROM Chat c WHERE c.itemId = :itemId AND c.active = true")
    @Override
    List<Chat> findByItemIdAndActiveTrue_AllChats(@Param("itemId") Integer itemId);

    @Override
    boolean existsByItemId(Integer itemId);
}
