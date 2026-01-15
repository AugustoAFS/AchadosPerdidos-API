package com.AchadosPerdidos.API.Application.Services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class S3Service {

    private static final Logger logger = LoggerFactory.getLogger(S3Service.class);
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;

    public S3Service(S3Client s3Client, S3Presigner s3Presigner, @Qualifier("s3BucketName") String bucketName) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
        this.bucketName = bucketName;
    }

    public String uploadFile(byte[] fileContent, String fileName, String contentType, String folder) {
        try {
            validateBucketConfiguration();

            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
            String uniqueId = UUID.randomUUID().toString();
            String fileExtension = getFileExtension(fileName);
            String s3Key = folder != null ? 
                folder + "/" + timestamp + "/" + uniqueId + fileExtension :
                timestamp + "/" + uniqueId + fileExtension;

            // Configurar metadados
            Map<String, String> metadata = new HashMap<>();
            metadata.put("original-name", fileName);
            metadata.put("upload-date", LocalDateTime.now().toString());
            metadata.put("content-type", contentType);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(contentType)
                    .contentLength((long) fileContent.length)
                    .metadata(metadata)
                    .build();

            // Fazer upload
            logger.debug("Fazendo upload para S3 - Bucket: {}, Key: {}", bucketName, s3Key);
            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileContent));
            logger.info("Upload concluído com sucesso - Key: {}", s3Key);

            return s3Key;

        } catch (NoSuchBucketException e) {
            logger.error("Bucket S3 não encontrado: {}. Verifique se o bucket existe e se o nome está correto na variável AWS_S3_BUCKET", bucketName);
            throw new IllegalStateException(
                String.format("Bucket S3 '%s' não existe. " +
                    "Por favor, crie o bucket no AWS S3 ou verifique se a variável de ambiente AWS_S3_BUCKET está configurada corretamente.", 
                    bucketName), e);
        } catch (Exception e) {
            logger.error("Erro ao fazer upload do arquivo para S3: {}", e.getMessage(), e);
            throw new RuntimeException("Erro ao fazer upload do arquivo para S3: " + e.getMessage(), e);
        }
    }

    public S3UploadResult uploadPhoto(byte[] fileContent, String fileName, String contentType, 
                                    Integer userId, Integer itemId, boolean isProfilePhoto) {
        try {
            String folder = isProfilePhoto ? "profile-photos" : "item-photos";
            String s3Key = uploadFile(fileContent, fileName, contentType, folder);
            
            return new S3UploadResult(
                s3Key,
                generateFileUrl(s3Key),
                fileName,
                contentType,
                (long) fileContent.length,
                userId,
                itemId,
                isProfilePhoto
            );
        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload da foto: " + e.getMessage(), e);
        }
    }

    public byte[] downloadFile(String s3Key) {
        try {
            validateBucketConfiguration();
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            return s3Client.getObject(getObjectRequest).readAllBytes();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao baixar arquivo do S3: " + e.getMessage(), e);
        }
    }

    public boolean fileExists(String s3Key) {
        try {
            validateBucketConfiguration();
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.headObject(headObjectRequest);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao verificar existência do arquivo: " + e.getMessage(), e);
        }
    }

    public boolean deleteFile(String s3Key) {
        try {
            validateBucketConfiguration();
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar arquivo do S3: " + e.getMessage(), e);
        }
    }

    private String generateFileUrl(String s3Key) {
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, s3Key);
    }

    public String generateSignedUrl(String s3Key, int expirationMinutes) {
        try {
            validateBucketConfiguration();
            
            if (s3Key == null || s3Key.isBlank()) {
                logger.warn("Tentativa de gerar Signed URL com chave S3 vazia");
                return null;
            }

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expirationMinutes))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            String signedUrl = presignedRequest.url().toString();
            
            logger.debug("Signed URL gerada para chave: {} (expira em {} minutos)", s3Key, expirationMinutes);
            return signedUrl;

        } catch (Exception e) {
            logger.error("Erro ao gerar Signed URL para chave {}: {}", s3Key, e.getMessage(), e);
            return null;
        }
    }

    public String generateSignedUrl(String s3Key) {
        return generateSignedUrl(s3Key, 60);
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex) : "";
    }

    private void validateBucketConfiguration() {
        if (bucketName == null || bucketName.isBlank()) {
            logger.error("Bucket S3 não configurado. Variável AWS_S3_BUCKET está vazia ou não definida.");
            throw new IllegalStateException(
                "Bucket S3 não configurado. " +
                "Defina a variável de ambiente AWS_S3_BUCKET com o nome do bucket. " +
                "Exemplo: AWS_S3_BUCKET=meu-bucket-fotos"
            );
        }
        logger.debug("Bucket S3 configurado: {}", bucketName);
    }
}
