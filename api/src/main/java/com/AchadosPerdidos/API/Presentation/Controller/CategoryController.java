package com.AchadosPerdidos.API.Presentation.Controller;

import com.AchadosPerdidos.API.Application.DTOs.Request.Category.CreateCategoryRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Category.CategoryResponseDTO;
import com.AchadosPerdidos.API.Application.Interfaces.ICategoryService;
import com.AchadosPerdidos.API.Application.Mapper.CategoryMapper;
import com.AchadosPerdidos.API.Domain.Entity.Category;

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
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Gerenciamento de categorias de itens")
public class CategoryController {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private CategoryMapper categoryMapper;

    @PostMapping
    @Operation(summary = "Criar categoria")
    public ResponseEntity<CategoryResponseDTO> create(@Valid @RequestBody CreateCategoryRequestDTO dto) {
        Category category = categoryMapper.fromCreate(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryMapper.toResponse(categoryService.create(category)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar categoria por ID")
    public ResponseEntity<CategoryResponseDTO> findById(
            @Parameter(description = "ID da categoria") @PathVariable Integer id) {
        return ResponseEntity.ok(categoryMapper.toResponse(categoryService.findById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar todas as categorias ativas")
    public ResponseEntity<List<CategoryResponseDTO>> findAll() {
        return ResponseEntity.ok(categoryMapper.toResponseList(categoryService.findAll()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar categoria")
    public ResponseEntity<CategoryResponseDTO> update(
            @Parameter(description = "ID da categoria") @PathVariable Integer id,
            @Valid @RequestBody CreateCategoryRequestDTO dto) {
        Category category = categoryMapper.fromCreate(dto);
        return ResponseEntity.ok(categoryMapper.toResponse(categoryService.update(id, category)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar categoria (soft-delete)")
    public ResponseEntity<Void> deactivate(
            @Parameter(description = "ID da categoria") @PathVariable Integer id) {
        categoryService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
