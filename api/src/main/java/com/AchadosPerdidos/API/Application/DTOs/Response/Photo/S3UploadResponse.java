package com.AchadosPerdidos.API.Application.DTOs.Response.Photo;

public record S3UploadResponse(
        String s3Key,
        String url,
        String originalName,
        String contentType,
        Long sizeBytes,
        Integer userId,
        Integer itemId,
        boolean isProfilePhoto) {
}
