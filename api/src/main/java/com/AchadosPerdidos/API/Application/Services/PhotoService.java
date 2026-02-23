package com.AchadosPerdidos.API.Application.Services;

import com.AchadosPerdidos.API.Application.Interfaces.IPhotoService;
import com.AchadosPerdidos.API.Application.Interfaces.Storage.IStorageService;
import com.AchadosPerdidos.API.Domain.Entity.Item_Photo;
import com.AchadosPerdidos.API.Domain.Entity.Photo;
import com.AchadosPerdidos.API.Domain.Entity.User_Photo;
import com.AchadosPerdidos.API.Domain.Repository.ItemPhotoRepository;
import com.AchadosPerdidos.API.Domain.Repository.PhotoRepository;
import com.AchadosPerdidos.API.Domain.Repository.UserPhotoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class PhotoService implements IPhotoService {

    private static final Logger log = LoggerFactory.getLogger(PhotoService.class);

    private static final String FOLDER_ITEMS = "item-photos";
    private static final String FOLDER_USERS = "profile-photos";

    @Autowired
    private IStorageService storageService;

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private ItemPhotoRepository itemPhotoRepository;

    @Autowired
    private UserPhotoRepository userPhotoRepository;

    @Override
    @Transactional
    public String uploadItemPhoto(Integer itemId, MultipartFile file) {
        String s3Key = doUpload(file, FOLDER_ITEMS);
        String url = storageService.generateSignedUrl(s3Key);

        Photo photo = buildPhoto(url, file);
        Photo saved = photoRepository.save(photo);

        Item_Photo link = new Item_Photo();
        link.setItemId(itemId);
        link.setPhotoId(saved.getId());
        itemPhotoRepository.save(link);

        log.info("Foto de item salva: itemId={} | photoId={} | key={}", itemId, saved.getId(), s3Key);
        return url;
    }

    @Override
    @Transactional
    public String uploadUserPhoto(Integer userId, MultipartFile file) {
        String s3Key = doUpload(file, FOLDER_USERS);
        String url = storageService.generateSignedUrl(s3Key);

        Photo photo = buildPhoto(url, file);
        Photo saved = photoRepository.save(photo);

        User_Photo link = new User_Photo();
        link.setUserId(userId);
        link.setPhotoId(saved.getId());
        userPhotoRepository.save(link);

        log.info("Foto de perfil salva: userId={} | photoId={} | key={}", userId, saved.getId(), s3Key);
        return url;
    }

    @Override
    @Transactional
    public void deleteItemPhoto(Integer itemId, Integer photoId) {
        itemPhotoRepository.findByItemIdAndActiveTrue(itemId).stream()
                .filter(ip -> ip.getPhotoId().equals(photoId))
                .findFirst()
                .ifPresent(ip -> {
                    photoRepository.findById(photoId).ifPresent(photo -> {
                        storageService.deleteFile(photo.getUrl());
                        photo.setActive(false);
                        photoRepository.save(photo);
                    });
                    itemPhotoRepository.deleteByItemIdAndPhotoId(itemId, photoId);
                    log.info("Foto de item removida: itemId={} | photoId={}", itemId, photoId);
                });
    }

    @Override
    @Transactional
    public void deleteUserPhoto(Integer userId, Integer photoId) {
        userPhotoRepository.findByUserIdAndActiveTrue(userId).stream()
                .filter(up -> up.getPhotoId().equals(photoId))
                .findFirst()
                .ifPresent(up -> {
                    photoRepository.findById(photoId).ifPresent(photo -> {
                        storageService.deleteFile(photo.getUrl());
                        photo.setActive(false);
                        photoRepository.save(photo);
                    });
                    userPhotoRepository.deleteByUserIdAndPhotoId(userId, photoId);
                    log.info("Foto de perfil removida: userId={} | photoId={}", userId, photoId);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getItemPhotoUrls(Integer itemId) {
        return itemPhotoRepository.findByItemIdAndActiveTrue(itemId).stream()
                .map(ip -> photoRepository.findById(ip.getPhotoId())
                        .map(Photo::getUrl).orElse(null))
                .filter(url -> url != null)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public String getUserPhotoUrl(Integer userId) {
        return userPhotoRepository.findFirstByUserIdAndActiveTrue(userId)
                .flatMap(up -> photoRepository.findById(up.getPhotoId()))
                .map(Photo::getUrl)
                .orElse(null);
    }

    private String doUpload(MultipartFile file, String folder) {
        try {
            return storageService.uploadFile(
                    file.getBytes(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    folder);
        } catch (IOException e) {
            throw new RuntimeException("Falha ao ler arquivo para upload: " + e.getMessage(), e);
        }
    }

    private Photo buildPhoto(String url, MultipartFile file) {
        Photo photo = new Photo();
        photo.setUrl(url);
        photo.setFileName(file.getOriginalFilename());
        photo.setSizeBytes(file.getSize());
        photo.setFileType(file.getContentType());
        return photo;
    }
}
