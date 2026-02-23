package com.AchadosPerdidos.API.Domain.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Item;
import com.AchadosPerdidos.API.Domain.Enum.Status_Item;
import com.AchadosPerdidos.API.Domain.Enum.Type_Item;

import java.util.List;

public interface IItemRepository {

    List<Item> findByStatusItemAndActiveTrue(Status_Item statusItem);

    List<Item> findByTypeItemAndActiveTrue(Type_Item typeItem);

    List<Item> findByCampusIdAndActiveTrue(Integer campusId);

    List<Item> findByCategoryIdAndActiveTrue(Integer categoryId);

    List<Item> findByAuthorUserIdAndActiveTrue(Integer authorUserId);

    List<Item> findByReceiverUserIdAndActiveTrue(Integer receiverUserId);

    List<Item> findActiveByCampusAndStatus(Integer campusId, Status_Item status);

    List<Item> searchByTermo(String termo);
}
