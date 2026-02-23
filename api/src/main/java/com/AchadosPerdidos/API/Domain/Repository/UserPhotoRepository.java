package com.AchadosPerdidos.API.Domain.Repository;

import com.AchadosPerdidos.API.Domain.Entity.User_Photo;
import com.AchadosPerdidos.API.Domain.Interfaces.IUserPhotoRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPhotoRepository extends BaseRepository<User_Photo, Integer>, IUserPhotoRepository {

    @Override
    List<User_Photo> findByUserIdAndActiveTrue(Integer userId);

    @Override
    Optional<User_Photo> findFirstByUserIdAndActiveTrue(Integer userId);

    @Override
    boolean existsByUserIdAndPhotoId(Integer userId, Integer photoId);

    @Override
    void deleteByUserIdAndPhotoId(Integer userId, Integer photoId);
}
