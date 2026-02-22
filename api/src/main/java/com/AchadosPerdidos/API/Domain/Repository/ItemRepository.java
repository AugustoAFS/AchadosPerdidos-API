package com.AchadosPerdidos.API.Domain.Repository;

import com.AchadosPerdidos.API.Domain.Entity.Item;
import com.AchadosPerdidos.API.Domain.Enum.Status_Item;
import com.AchadosPerdidos.API.Domain.Enum.Type_Item;
import com.AchadosPerdidos.API.Domain.Interfaces.IItemRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends BaseRepository<Item, Integer>, IItemRepository {

        String QUERY_FIND_ACTIVE_BY_CAMPUS_AND_STATUS = "SELECT i FROM Item i"
                        + " WHERE i.campusId = :campusId"
                        + "   AND i.statusItem = :status"
                        + "   AND i.active = true"
                        + " ORDER BY i.postedAt DESC";

        String QUERY_SEARCH_BY_TERMO = "SELECT i FROM Item i"
                        + " WHERE i.active = true"
                        + "   AND (LOWER(i.title) LIKE LOWER(CONCAT('%', :termo, '%'))"
                        + "     OR LOWER(i.description) LIKE LOWER(CONCAT('%', :termo, '%')))";

        @Override
        List<Item> findByStatusItemAndActiveTrue(Status_Item statusItem);

        @Override
        List<Item> findByTypeItemAndActiveTrue(Type_Item typeItem);

        @Override
        List<Item> findByCampusIdAndActiveTrue(Integer campusId);

        @Override
        List<Item> findByCategoryIdAndActiveTrue(Integer categoryId);

        @Override
        List<Item> findByAuthorUserIdAndActiveTrue(Integer authorUserId);

        @Override
        List<Item> findByReceiverUserIdAndActiveTrue(Integer receiverUserId);

        @Override
        @Query(QUERY_FIND_ACTIVE_BY_CAMPUS_AND_STATUS)
        List<Item> findActiveByCampusAndStatus(
                        @Param("campusId") Integer campusId,
                        @Param("status") Status_Item status);

        @Override
        @Query(QUERY_SEARCH_BY_TERMO)
        List<Item> searchByTermo(@Param("termo") String termo);
}
