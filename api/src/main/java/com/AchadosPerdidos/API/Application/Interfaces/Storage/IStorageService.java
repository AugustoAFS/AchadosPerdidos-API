package com.AchadosPerdidos.API.Application.Interfaces.Storage;

public interface IStorageService {

    String uploadFile(byte[] fileContent, String fileName, String contentType, String folder);

    byte[] downloadFile(String fileKey);

    boolean fileExists(String fileKey);

    boolean deleteFile(String fileKey);

    String generateSignedUrl(String fileKey, int expirationMinutes);

    default String generateSignedUrl(String fileKey) {
        return generateSignedUrl(fileKey, 60);
    }
}
