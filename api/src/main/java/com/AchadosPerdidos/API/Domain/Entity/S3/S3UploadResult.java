package com.AchadosPerdidos.API.Domain.Entity.S3;

import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

@Getter
@Setter
public public class S3UploadResult {
    private final String s3Key;
    private final String fileUrl;
    private final String originalName;
    private final String contentType;
    private final Long fileSize;
    private final Integer userId;
    private final Integer itemId;
    private final boolean isProfilePhoto;

    public S3UploadResult(String s3Key, String fileUrl, String originalName, String contentType,
                          Long fileSize, Integer userId, Integer itemId, boolean isProfilePhoto) {
        this.s3Key = s3Key;
        this.fileUrl = fileUrl;
        this.originalName = originalName;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.userId = userId;
        this.itemId = itemId;
        this.isProfilePhoto = isProfilePhoto;
    }
    
}