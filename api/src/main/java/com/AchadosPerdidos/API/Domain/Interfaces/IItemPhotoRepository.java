package com.AchadosPerdidos.API.Domain.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Item_Photo;

import java.util.List;

public interface IItemPhotoRepository {

    List<Item_Photo> findByItemIdAndActiveTrue(Integer itemId);

    void deleteByItemIdAndPhotoId(Integer itemId, Integer photoId);

    boolean existsByItemIdAndPhotoId(Integer itemId, Integer photoId);
}
