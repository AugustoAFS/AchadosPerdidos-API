package com.AchadosPerdidos.API.Domain.Repository;

import com.AchadosPerdidos.API.Domain.Entity.Photo;
import com.AchadosPerdidos.API.Domain.Interfaces.IPhotoRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface PhotoRepository extends BaseRepository<Photo, Integer>, IPhotoRepository {

    @Override
    boolean existsByUrl(String url);
}
