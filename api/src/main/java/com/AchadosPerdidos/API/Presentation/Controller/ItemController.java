package com.AchadosPerdidos.API.Presentation.Controller;

import com.AchadosPerdidos.API.Application.DTOs.Request.Item.CreateItemRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Request.Item.UpdateItemRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Request.Item.UpdateItemStatusRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Item.ItemResponseDTO;
import com.AchadosPerdidos.API.Application.Interfaces.IItemService;
import com.AchadosPerdidos.API.Application.Mapper.ItemMapper;
import com.AchadosPerdidos.API.Domain.Entity.Item;
import com.AchadosPerdidos.API.Domain.Enum.Status_Item;
import com.AchadosPerdidos.API.Domain.Enum.Type_Item;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@Tag(name = "Items", description = "Gerenciamento de itens achados e perdidos")
public class ItemController {

    @Autowired
    private IItemService itemService;

    @Autowired
    private ItemMapper itemMapper;

    @PostMapping
    @Operation(summary = "Publicar item", description = "Cria um novo item achado ou perdido")
    public ResponseEntity<ItemResponseDTO> create(@Valid @RequestBody CreateItemRequestDTO dto) {
        Item item = itemMapper.fromCreate(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(itemMapper.toResponse(itemService.create(item)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar item por ID")
    public ResponseEntity<ItemResponseDTO> findById(
            @Parameter(description = "ID do item") @PathVariable Integer id) {
        return ResponseEntity.ok(itemMapper.toResponse(itemService.findById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar todos os itens ativos")
    public ResponseEntity<List<ItemResponseDTO>> findAll() {
        return ResponseEntity.ok(itemMapper.toResponseList(itemService.findAll()));
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
