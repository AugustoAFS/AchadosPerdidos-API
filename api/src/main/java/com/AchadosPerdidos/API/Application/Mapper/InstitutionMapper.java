package com.AchadosPerdidos.API.Application.Mapper;

import com.AchadosPerdidos.API.Application.DTOs.Request.Institution.CreateInstitutionRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Institution.InstitutionResponseDTO;
import com.AchadosPerdidos.API.Domain.Entity.Institution;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InstitutionMapper {

    public InstitutionResponseDTO toResponse(Institution institution) {
        if (institution == null)
            return null;

        InstitutionResponseDTO dto = new InstitutionResponseDTO();
        dto.setId(institution.getId());
        dto.setName(institution.getName());
        dto.setPrefix(institution.getPrefix());
        dto.setCnpj(institution.getCnpj());
        return dto;
    }

    public List<InstitutionResponseDTO> toResponseList(List<Institution> institutions) {
        if (institutions == null)
            return List.of();
        return institutions.stream().map(this::toResponse).toList();
    }

    public Institution fromCreate(CreateInstitutionRequestDTO dto) {
        if (dto == null)
            return null;

        Institution institution = new Institution();
        institution.setName(dto.getName());
        institution.setPrefix(dto.getPrefix());
        institution.setCnpj(dto.getCnpj());
        return institution;
    }
}
