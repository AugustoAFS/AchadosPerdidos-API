package com.AchadosPerdidos.API.Application.Services.Photo;

import com.AchadosPerdidos.API.Application.Exception.StorageException;
import com.AchadosPerdidos.API.Application.Interfaces.Storage.IStorageService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService implements IStorageService {

    private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadFile(byte[] fileContent, String fileName, String contentType, String folder) {
        try {
            Map params = ObjectUtils.asMap(
                    "folder", folder,
                    "use_filename", true,
                    "unique_filename", true,
                    "overwrite", true);

            Map uploadResult = cloudinary.uploader().upload(fileContent, params);
            String url = (String) uploadResult.get("secure_url");

            log.info("Upload feito com sucesso no Cloudinary, URL: {}", url);

            // Retorna o Public ID para sabermos gerenciar ou apenas a URL?
            // A interface espera uma "fileKey". Para Cloudinary, podemos retornar o
            // public_id ou a própria URL.
            // Para mantermos a url como identificador e facilitar o uso, retornamos a
            // própria string da URL.
            // Mas para o `deleteFile` precisaremos do public_id.
            return (String) uploadResult.get("public_id");
        } catch (IOException e) {
            log.error("Erro ao fazer upload no Cloudinary: {}", e.getMessage());
            throw new StorageException("Erro ao fazer upload da imagem", e);
        }
    }

    @Override
    public byte[] downloadFile(String fileKey) {
        throw new UnsupportedOperationException(
                "O download via bytestream não está disponível para Cloudinary nesta implementação.");
    }

    @Override
    public boolean fileExists(String fileKey) {
        try {
            Map result = cloudinary.api().resource(fileKey, ObjectUtils.emptyMap());
            return result != null && result.containsKey("public_id");
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteFile(String fileKey) {
        try {
            String publicId = extractPublicId(fileKey);
            Map result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            return "ok".equals(result.get("result"));
        } catch (IOException e) {
            log.error("Erro ao deletar imagem no Cloudinary: {}", e.getMessage());
            return false;
        }
    }

    private String extractPublicId(String url) {
        if (url == null || url.isEmpty())
            return url;
        int uploadIndex = url.indexOf("/upload/");
        if (uploadIndex == -1)
            return url;

        String afterUpload = url.substring(uploadIndex + 8);
        if (afterUpload.matches("v\\d+/.*")) {
            afterUpload = afterUpload.substring(afterUpload.indexOf("/") + 1);
        }

        int lastDot = afterUpload.lastIndexOf(".");
        return lastDot != -1 ? afterUpload.substring(0, lastDot) : afterUpload;
    }

    @Override
    public String generateSignedUrl(String fileKey, int expirationMinutes) {
        // Cloudinary gera URLs assinadas só se for privado. Para recursos públicos,
        // retornamos a URL pública:
        try {
            return cloudinary.url().generate(fileKey);
        } catch (Exception e) {
            log.error("Erro ao gerar URL do Cloudinary para chave {}: {}", fileKey, e.getMessage());
            return null;
        }
    }

    @Override
    public String generateSignedUrl(String fileKey) {
        return cloudinary.url().generate(fileKey);
    }
}
