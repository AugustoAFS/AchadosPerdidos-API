package com.AchadosPerdidos.API.Application.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Item;
import com.AchadosPerdidos.API.Domain.Enum.Status_Item;
import com.AchadosPerdidos.API.Domain.Enum.Type_Item;

import java.util.List;

public interface IItemService extends IBaseService<Item, Integer> {

    List<Item> findByCampus(Integer campusId);

    List<Item> findByStatus(Status_Item status);

    List<Item> findByType(Type_Item type);

    List<Item> findByCategory(Integer categoryId);

    List<Item> findByAuthor(Integer authorUserId);

    List<Item> findByCampusAndStatus(Integer campusId, Status_Item status);

    List<Item> search(String termo);

    Item updateStatus(Integer id, Status_Item newStatus);
}
