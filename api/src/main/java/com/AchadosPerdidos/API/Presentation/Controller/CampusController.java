package com.AchadosPerdidos.API.Presentation.Controller;

import com.AchadosPerdidos.API.Application.DTOs.Request.Campus.CreateCampusRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Campus.CampusResponseDTO;
import com.AchadosPerdidos.API.Application.Interfaces.ICampusService;
import com.AchadosPerdidos.API.Application.Mapper.CampusMapper;
import com.AchadosPerdidos.API.Domain.Entity.Campus;

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
@RequestMapping("/api/campuses")
@Tag(name = "Campuses", description = "Gerenciamento de campi")
public class CampusController {

    @Autowired
    private ICampusService campusService;

    @Autowired
    private CampusMapper campusMapper;

    @PostMapping
    @Operation(summary = "Criar campus")
    public ResponseEntity<CampusResponseDTO> create(@Valid @RequestBody CreateCampusRequestDTO dto) {
        Campus campus = campusMapper.fromCreate(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(campusMapper.toResponse(campusService.create(campus)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar campus por ID")
    public ResponseEntity<CampusResponseDTO> findById(
            @Parameter(description = "ID do campus") @PathVariable Integer id) {
        return ResponseEntity.ok(campusMapper.toResponse(campusService.findById(id)));
    }

    @GetMapping
    @Operation(summary = "Listar todos os campi ativos")
    public ResponseEntity<List<CampusResponseDTO>> findAll() {
        return ResponseEntity.ok(campusMapper.toResponseList(campusService.findAll()));
    }

    @GetMapping("/institution/{institutionId}")
    @Operation(summary = "Listar campi por instituição")
    public ResponseEntity<List<CampusResponseDTO>> findByInstitution(
            @Parameter(description = "ID da instituição") @PathVariable Integer institutionId) {
        return ResponseEntity.ok(campusMapper.toResponseList(campusService.findByInstitution(institutionId)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar campus")
    public ResponseEntity<CampusResponseDTO> update(
            @Parameter(description = "ID do campus") @PathVariable Integer id,
            @Valid @RequestBody CreateCampusRequestDTO dto) {
        Campus campus = campusMapper.fromCreate(dto);
        return ResponseEntity.ok(campusMapper.toResponse(campusService.update(id, campus)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Desativar campus (soft-delete)")
    public ResponseEntity<Void> deactivate(
            @Parameter(description = "ID do campus") @PathVariable Integer id) {
        campusService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
