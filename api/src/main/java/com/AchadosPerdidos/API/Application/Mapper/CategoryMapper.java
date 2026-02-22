package com.AchadosPerdidos.API.Application.Mapper;

import com.AchadosPerdidos.API.Application.DTOs.Request.Category.CreateCategoryRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Category.CategoryResponseDTO;
import com.AchadosPerdidos.API.Domain.Entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

    public CategoryResponseDTO toResponse(Category category) {
        if (category == null)
            return null;

        CategoryResponseDTO dto = new CategoryResponseDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    public List<CategoryResponseDTO> toResponseList(List<Category> categories) {
        if (categories == null)
            return List.of();
        return categories.stream().map(this::toResponse).toList();
    }

    public Category fromCreate(CreateCategoryRequestDTO dto) {
        if (dto == null)
            return null;

        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }
}
