package com.AchadosPerdidos.API.Application.Mapper;

import com.AchadosPerdidos.API.Application.DTOs.Request.Item.CreateItemRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Request.Item.UpdateItemRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Item.ItemResponseDTO;
import com.AchadosPerdidos.API.Application.Interfaces.IPhotoService;
import com.AchadosPerdidos.API.Domain.Entity.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ItemMapper {

    @Autowired
    private IPhotoService photoService;

    public ItemResponseDTO toResponse(Item item) {
        if (item == null)
            return null;

        ItemResponseDTO dto = new ItemResponseDTO();
        dto.setId(item.getId());
        dto.setTitle(item.getTitle());
        dto.setDescription(item.getDescription());
        dto.setTypeItem(item.getTypeItem());
        dto.setStatusItem(item.getStatusItem());
        dto.setMeetingLocation(item.getMeetingLocation());
        dto.setPostedAt(item.getPostedAt());
        dto.setDeliveredAt(item.getDeliveredAt());
        dto.setCampusId(item.getCampusId());
        dto.setCategoryId(item.getCategoryId());
        dto.setAuthorUserId(item.getAuthorUserId());
        dto.setReceiverUserId(item.getReceiverUserId());
        // Popula as URLs das fotos automaticamente
        if (item.getId() != null) {
            dto.setPhotoUrls(photoService.getItemPhotoUrls(item.getId()));
        }
        return dto;
    }

    public List<ItemResponseDTO> toResponseList(List<Item> items) {
        if (items == null)
            return List.of();
        return items.stream().map(this::toResponse).toList();
    }

    public Item fromCreate(CreateItemRequestDTO dto) {
        if (dto == null)
            return null;

        Item item = new Item();
        item.setTitle(dto.getTitle());
        item.setDescription(dto.getDescription());
        item.setTypeItem(dto.getTypeItem());
        item.setMeetingLocation(dto.getMeetingLocation());
        item.setCampusId(dto.getCampusId());
        item.setCategoryId(dto.getCategoryId());
        return item;
    }

    public void applyUpdate(Item item, UpdateItemRequestDTO dto) {
        if (item == null || dto == null)
            return;

        if (dto.getTitle() != null)
            item.setTitle(dto.getTitle());
        if (dto.getDescription() != null)
            item.setDescription(dto.getDescription());
        if (dto.getMeetingLocation() != null)
            item.setMeetingLocation(dto.getMeetingLocation());
        if (dto.getCategoryId() != null)
            item.setCategoryId(dto.getCategoryId());
    }
}
