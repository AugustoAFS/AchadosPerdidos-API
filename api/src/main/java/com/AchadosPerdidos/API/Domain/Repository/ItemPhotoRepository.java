package com.AchadosPerdidos.API.Domain.Repository;

import com.AchadosPerdidos.API.Domain.Entity.Item_Photo;
import com.AchadosPerdidos.API.Domain.Interfaces.IItemPhotoRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemPhotoRepository extends BaseRepository<Item_Photo, Integer>, IItemPhotoRepository {

    @Override
    List<Item_Photo> findByItemIdAndActiveTrue(Integer itemId);

    @Override
    void deleteByItemIdAndPhotoId(Integer itemId, Integer photoId);

    @Override
    boolean existsByItemIdAndPhotoId(Integer itemId, Integer photoId);
}
