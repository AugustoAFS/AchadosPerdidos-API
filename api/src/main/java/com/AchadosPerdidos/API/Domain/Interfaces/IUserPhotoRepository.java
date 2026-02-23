package com.AchadosPerdidos.API.Domain.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.User_Photo;

import java.util.List;
import java.util.Optional;

public interface IUserPhotoRepository {

    List<User_Photo> findByUserIdAndActiveTrue(Integer userId);

    Optional<User_Photo> findFirstByUserIdAndActiveTrue(Integer userId);

    boolean existsByUserIdAndPhotoId(Integer userId, Integer photoId);

    void deleteByUserIdAndPhotoId(Integer userId, Integer photoId);
}
