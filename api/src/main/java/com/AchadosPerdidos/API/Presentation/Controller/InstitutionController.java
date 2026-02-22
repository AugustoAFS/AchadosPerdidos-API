package com.AchadosPerdidos.API.Presentation.Controller;

import com.AchadosPerdidos.API.Application.DTOs.Request.Institution.CreateInstitutionRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Institution.InstitutionResponseDTO;
import com.AchadosPerdidos.API.Application.Interfaces.IInstitutionService;
import com.AchadosPerdidos.API.Application.Mapper.InstitutionMapper;
import com.AchadosPerdidos.API.Domain.Entity.Institution;

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
@RequestMapping("/api/institutions")
@Tag(name = "Institutions", description = "Gerenciamento de instituições")
public class InstitutionController {

    @Autowired
    private IInstitutionService institutionService;

    @Autowired
    private InstitutionMapper institutionMapper;

    @PostMapping
    @Operation(summary = "Criar instituição")
    public ResponseEntity<InstitutionResponseDTO> create(@Valid @RequestBody CreateInstitutionRequestDTO dto) {
        Institution institution = institutionMapper.fromCreate(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(institutionMapper.toResponse(institutionService.create(institution)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar instituição por ID")
    public ResponseEntity<InstitutionResponseDTO> findById(
            @Parameter(description = "ID da instituição") @PathVariable Integer id) {
        return ResponseEntity.ok(institutionMapper.toResponse(institutionService.findById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar todas as instituições ativas")
    public ResponseEntity<List<InstitutionResponseDTO>> findAll() {
        return ResponseEntity.ok(institutionMapper.toResponseList(institutionService.findAll()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar instituição")
    public ResponseEntity<InstitutionResponseDTO> update(
            @Parameter(description = "ID da instituição") @PathVariable Integer id,
            @Valid @RequestBody CreateInstitutionRequestDTO dto) {
        Institution institution = institutionMapper.fromCreate(dto);
        return ResponseEntity.ok(institutionMapper.toResponse(institutionService.update(id, institution)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar instituição (soft-delete)")
    public ResponseEntity<Void> deactivate(
            @Parameter(description = "ID da instituição") @PathVariable Integer id) {
        institutionService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
