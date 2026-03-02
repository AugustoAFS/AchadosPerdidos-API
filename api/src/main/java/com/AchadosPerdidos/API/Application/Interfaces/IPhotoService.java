package com.AchadosPerdidos.API.Application.Interfaces;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPhotoService {

    String uploadItemPhoto(Integer itemId, MultipartFile file);

    List<String> uploadItemPhotos(Integer itemId, List<MultipartFile> files);

    String uploadUserPhoto(Integer userId, MultipartFile file);

    void deleteItemPhoto(Integer itemId, Integer photoId);

    void deleteUserPhoto(Integer userId, Integer photoId);

    List<String> getItemPhotoUrls(Integer itemId);

    String getUserPhotoUrl(Integer userId);
}
