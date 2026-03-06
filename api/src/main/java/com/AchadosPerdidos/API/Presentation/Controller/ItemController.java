package com.AchadosPerdidos.API.Presentation.Controller;

import com.AchadosPerdidos.API.Application.DTOs.Request.Item.CreateItemRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Request.Item.UpdateItemRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Request.Item.UpdateItemStatusRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Item.ItemResponseDTO;
import com.AchadosPerdidos.API.Application.Exception.BusinessException;
import com.AchadosPerdidos.API.Application.Interfaces.IItemService;
import com.AchadosPerdidos.API.Application.Interfaces.IPhotoService;
import com.AchadosPerdidos.API.Application.Mapper.ItemMapper;
import com.AchadosPerdidos.API.Application.Services.NotificationService;
import com.AchadosPerdidos.API.Domain.Entity.Item;
import com.AchadosPerdidos.API.Domain.Enum.Status_Item;
import com.AchadosPerdidos.API.Domain.Enum.Type_Item;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@Tag(name = "Items", description = "Gerenciamento de itens achados e perdidos")
public class ItemController {

    @Autowired
    private IItemService itemService;

    @Autowired
    private IPhotoService photoService;

    @Autowired
    private ItemMapper itemMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationService notificationService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Publicar item", description = "Cria um novo item achado ou perdido. " +
            "Para itens FIND (achados), ao menos uma foto é obrigatória. " +
            "Para itens LOST (perdidos), a foto é opcional. " +
            "Envie o JSON do item no campo 'data' e as fotos no campo 'photos'.")
    public ResponseEntity<ItemResponseDTO> create(
            @Parameter(description = "JSON do item (CreateItemRequestDTO)") @RequestPart("data") String dataJson,
            @Parameter(description = "Fotos do item (opcional para LOST, obrigatória para FIND)") @RequestPart(value = "photos", required = false) List<MultipartFile> photos)
            throws Exception {

        CreateItemRequestDTO dto = objectMapper.readValue(dataJson, CreateItemRequestDTO.class);

        // Validação manual do DTO (substituindo @Valid já que veio como String)
        jakarta.validation.Validator validator = jakarta.validation.Validation
                .buildDefaultValidatorFactory().getValidator();
        var violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            String msg = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(java.util.stream.Collectors.joining("; "));
            throw new BusinessException("Dados inválidos: " + msg);
        }

        // Filtra arquivos vazios da lista
        List<MultipartFile> validPhotos = (photos == null) ? Collections.emptyList()
                : photos.stream().filter(f -> f != null && !f.isEmpty()).toList();

        // Regra de negócio: item FIND exige ao menos uma foto
        if (dto.getTypeItem() == Type_Item.FIND && validPhotos.isEmpty()) {
            throw new BusinessException(
                    "Itens do tipo ACHADO (FIND) exigem ao menos uma foto para identificação.");
        }

        // Cria o item
        Item item = itemMapper.fromCreate(dto);
        Item saved = itemService.create(item);

        // Faz upload das fotos e vincula via item_photo
        if (!validPhotos.isEmpty()) {
            photoService.uploadItemPhotos(saved.getId(), validPhotos);
        }

        ItemResponseDTO response = itemMapper.toResponse(saved);
        response.setPhotoUrls(photoService.getItemPhotoUrls(saved.getId()));

        // Notifica usuários do campus sobre o novo item (assíncrono — não bloqueia a
        // resposta)
        notificationService.notifyNewItem(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar item por ID", description = "Retorna o item com suas URLs de fotos incluídas")
    public ResponseEntity<ItemResponseDTO> findById(
            @Parameter(description = "ID do item") @PathVariable Integer id) {
        return ResponseEntity.ok(withPhotos(itemMapper.toResponse(itemService.findById(id))));
    }

    /** Popula photoUrls no DTO a partir do serviço de fotos. */
    private ItemResponseDTO withPhotos(ItemResponseDTO dto) {
        if (dto != null && dto.getId() != null) {
            dto.setPhotoUrls(photoService.getItemPhotoUrls(dto.getId()));
        }
        return dto;
    }

    @GetMapping
    @Operation(summary = "Listar todos os itens ativos (exceto entregues)")
    public ResponseEntity<List<ItemResponseDTO>> findAll() {
        List<Item> items = itemService.findAll().stream()
                .filter(i -> i.getStatusItem() != Status_Item.ENTREGUE)
                .toList();
        return ResponseEntity.ok(itemMapper.toResponseList(items));
    }

    @GetMapping("/campus/{campusId}")
    @Operation(summary = "Listar itens por campus")
    public ResponseEntity<List<ItemResponseDTO>> findByCampus(
            @Parameter(description = "ID do campus") @PathVariable Integer campusId) {
        return ResponseEntity.ok(itemMapper.toResponseList(itemService.findByCampus(campusId)));
    }

    @GetMapping("/campus/{campusId}/status/{status}")
    @Operation(summary = "Listar itens por campus e status")
    public ResponseEntity<List<ItemResponseDTO>> findByCampusAndStatus(
            @Parameter(description = "ID do campus") @PathVariable Integer campusId,
            @Parameter(description = "Status do item") @PathVariable Status_Item status) {
        return ResponseEntity.ok(itemMapper.toResponseList(itemService.findByCampusAndStatus(campusId, status)));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar itens por status")
    public ResponseEntity<List<ItemResponseDTO>> findByStatus(
            @Parameter(description = "Status do item") @PathVariable Status_Item status) {
        return ResponseEntity.ok(itemMapper.toResponseList(itemService.findByStatus(status)));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Listar itens por tipo")
    public ResponseEntity<List<ItemResponseDTO>> findByType(
            @Parameter(description = "Tipo do item") @PathVariable Type_Item type) {
        return ResponseEntity.ok(itemMapper.toResponseList(itemService.findByType(type)));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Listar itens por categoria")
    public ResponseEntity<List<ItemResponseDTO>> findByCategory(
            @Parameter(description = "ID da categoria") @PathVariable Integer categoryId) {
        return ResponseEntity.ok(itemMapper.toResponseList(itemService.findByCategory(categoryId)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Listar itens publicados por um usuário")
    public ResponseEntity<List<ItemResponseDTO>> findByAuthor(
            @Parameter(description = "ID do usuário autor") @PathVariable Integer userId) {
        return ResponseEntity.ok(itemMapper.toResponseList(itemService.findByAuthor(userId)));
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar itens por termo", description = "Busca por título ou descrição")
    public ResponseEntity<List<ItemResponseDTO>> search(
            @Parameter(description = "Termo de busca") @RequestParam String q) {
        return ResponseEntity.ok(itemMapper.toResponseList(itemService.search(q)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar item")
    public ResponseEntity<ItemResponseDTO> update(
            @Parameter(description = "ID do item") @PathVariable Integer id,
            @Valid @RequestBody UpdateItemRequestDTO dto) {
        Item existing = itemService.findById(id);
        itemMapper.applyUpdate(existing, dto);
        return ResponseEntity.ok(itemMapper.toResponse(itemService.update(id, existing)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar status do item", description = "Muda o status do item (ex: PERDIDO → ENCONTRADO → ENTREGUE)")
    public ResponseEntity<ItemResponseDTO> updateStatus(
            @Parameter(description = "ID do item") @PathVariable Integer id,
            @Valid @RequestBody UpdateItemStatusRequestDTO dto) {
        return ResponseEntity.ok(itemMapper.toResponse(itemService.updateStatus(id, dto.getStatus())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar item (soft-delete)")
    public ResponseEntity<Void> deactivate(
            @Parameter(description = "ID do item") @PathVariable Integer id) {
        itemService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
