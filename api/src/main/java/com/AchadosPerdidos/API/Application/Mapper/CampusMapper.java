package com.AchadosPerdidos.API.Application.Mapper;

import com.AchadosPerdidos.API.Application.DTOs.Request.Campus.CreateCampusRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Campus.CampusResponseDTO;
import com.AchadosPerdidos.API.Domain.Entity.Campus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampusMapper {

    public CampusResponseDTO toResponse(Campus campus) {
        if (campus == null)
            return null;

        CampusResponseDTO dto = new CampusResponseDTO();
        dto.setId(campus.getId());
        dto.setName(campus.getName());
        dto.setInstitutionId(campus.getInstitutionId());
        return dto;
    }

    public List<CampusResponseDTO> toResponseList(List<Campus> campuses) {
        if (campuses == null)
            return List.of();
        return campuses.stream().map(this::toResponse).toList();
    }

    public Campus fromCreate(CreateCampusRequestDTO dto) {
        if (dto == null)
            return null;

        Campus campus = new Campus();
        campus.setName(dto.getName());
        campus.setInstitutionId(dto.getInstitutionId());
        return campus;
    }
}
