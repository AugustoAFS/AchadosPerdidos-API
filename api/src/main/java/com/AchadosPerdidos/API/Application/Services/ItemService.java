package com.AchadosPerdidos.API.Application.Services;

import com.AchadosPerdidos.API.Application.Interfaces.IItemService;
import com.AchadosPerdidos.API.Domain.Entity.Item;
import com.AchadosPerdidos.API.Domain.Enum.Status_Item;
import com.AchadosPerdidos.API.Domain.Enum.Type_Item;
import com.AchadosPerdidos.API.Domain.Repository.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ItemService extends BaseService<Item, Integer, ItemRepository>
        implements IItemService {

    private static final Logger log = LoggerFactory.getLogger(ItemService.class);

    public ItemService(ItemRepository repository) {
        super(repository);
    }

    @Override
    @Transactional
    public Item create(Item item) {
        // Seta o autor a partir do usuário autenticado (userId guardado como
        // credentials no JWT filter)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getCredentials() != null) {
            item.setAuthorUserId(Integer.valueOf(auth.getCredentials().toString()));
        }

        item.setPostedAt(LocalDateTime.now());
        item.setStatusItem(Status_Item.PERDIDO);
        log.info("Criando item: {} (author={})", item.getTitle(), item.getAuthorUserId());
        return repository.save(item);
    }

    @Override
    @Transactional
    public Item update(Integer id, Item data) {
        Item existing = findById(id);
        existing.setTitle(data.getTitle());
        existing.setDescription(data.getDescription());
        existing.setTypeItem(data.getTypeItem());
        existing.setCategoryId(data.getCategoryId());
        existing.setMeetingLocation(data.getMeetingLocation());
        log.info("Atualizando item id={}", id);
        return repository.save(existing);
    }

    @Override
    @Transactional
    public void deactivate(Integer id) {
        Item item = findById(id);
        item.setActive(false);
        item.setDeletedAt(LocalDateTime.now());
        repository.save(item);
        log.info("Item desativado id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findByCampus(Integer campusId) {
        return repository.findByCampusIdAndActiveTrue(campusId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findByStatus(Status_Item status) {
        return repository.findByStatusItemAndActiveTrue(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findByType(Type_Item type) {
        return repository.findByTypeItemAndActiveTrue(type);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findByCategory(Integer categoryId) {
        return repository.findByCategoryIdAndActiveTrue(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findByAuthor(Integer authorUserId) {
        return repository.findByAuthorUserIdAndActiveTrue(authorUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findByCampusAndStatus(Integer campusId, Status_Item status) {
        return repository.findActiveByCampusAndStatus(campusId, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> search(String termo) {
        return repository.searchByTermo(termo);
    }

    @Override
    @Transactional
    public Item updateStatus(Integer id, Status_Item newStatus) {
        Item item = findById(id);
        item.setStatusItem(newStatus);
        if (newStatus == Status_Item.ENTREGUE) {
            item.setDeliveredAt(LocalDateTime.now());
        }
        log.info("Status do item id={} atualizado para {}", id, newStatus);
        return repository.save(item);
    }
}
