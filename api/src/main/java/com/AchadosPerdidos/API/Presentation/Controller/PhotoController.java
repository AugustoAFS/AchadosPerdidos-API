package com.AchadosPerdidos.API.Presentation.Controller;

import com.AchadosPerdidos.API.Application.Interfaces.IPhotoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/photos")
@Tag(name = "Photos", description = "Upload e remoção de fotos de itens e perfis de usuários")
public class PhotoController {

    @Autowired
    private IPhotoService photoService;

    @PostMapping("/items/{itemId}")
    @Operation(summary = "Upload de foto de item", description = "Envia uma imagem para o Cloudinary e associa ao item")
    public ResponseEntity<String> uploadItemPhoto(
            @Parameter(description = "ID do item") @PathVariable Integer itemId,
            @Parameter(description = "Arquivo de imagem") @RequestParam("file") MultipartFile file) {
        String url = photoService.uploadItemPhoto(itemId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

    @GetMapping("/items/{itemId}")
    @Operation(summary = "Listar URLs de fotos de um item")
    public ResponseEntity<List<String>> getItemPhotos(
            @Parameter(description = "ID do item") @PathVariable Integer itemId) {
        return ResponseEntity.ok(photoService.getItemPhotoUrls(itemId));
    }

    @DeleteMapping("/items/{itemId}/{photoId}")
    @Operation(summary = "Remover foto de item", description = "Remove a foto do Cloudinary e desfaz a associação com o item")
    public ResponseEntity<Void> deleteItemPhoto(
            @Parameter(description = "ID do item") @PathVariable Integer itemId,
            @Parameter(description = "ID da foto") @PathVariable Integer photoId) {
        photoService.deleteItemPhoto(itemId, photoId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/{userId}")
    @Operation(summary = "Upload de foto de perfil do usuário")
    public ResponseEntity<String> uploadUserPhoto(
            @Parameter(description = "ID do usuário") @PathVariable Integer userId,
            @Parameter(description = "Arquivo de imagem") @RequestParam("file") MultipartFile file) {
        String url = photoService.uploadUserPhoto(userId, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(url);
    }

    @GetMapping("/users/{userId}")
    @Operation(summary = "Obter URL da foto de perfil do usuário")
    public ResponseEntity<String> getUserPhoto(
            @Parameter(description = "ID do usuário") @PathVariable Integer userId) {
        String url = photoService.getUserPhotoUrl(userId);
        return url != null ? ResponseEntity.ok(url) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/users/{userId}/{photoId}")
    @Operation(summary = "Remover foto de perfil do usuário")
    public ResponseEntity<Void> deleteUserPhoto(
            @Parameter(description = "ID do usuário") @PathVariable Integer userId,
            @Parameter(description = "ID da foto") @PathVariable Integer photoId) {
        photoService.deleteUserPhoto(userId, photoId);
        return ResponseEntity.noContent().build();
    }
}
