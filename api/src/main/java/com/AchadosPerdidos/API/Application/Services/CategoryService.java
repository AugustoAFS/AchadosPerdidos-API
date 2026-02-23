package com.AchadosPerdidos.API.Application.Services;

import com.AchadosPerdidos.API.Application.Interfaces.ICategoryService;
import com.AchadosPerdidos.API.Domain.Entity.Category;
import com.AchadosPerdidos.API.Domain.Repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CategoryService extends BaseService<Category, Integer, CategoryRepository>
        implements ICategoryService {

    public CategoryService(CategoryRepository repository) {
        super(repository);
    }

    @Override
    @Transactional
    public Category create(Category category) {
        if (repository.existsByName(category.getName())) {
            throw new IllegalArgumentException("Categoria já existe: " + category.getName());
        }
        return repository.save(category);
    }

    @Override
    @Transactional
    public Category update(Integer id, Category data) {
        Category existing = findById(id);
        existing.setName(data.getName());
        return repository.save(existing);
    }

    @Override
    @Transactional
    public void deactivate(Integer id) {
        Category category = findById(id);
        category.setActive(false);
        category.setDeletedAt(LocalDateTime.now());
        repository.save(category);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return repository.existsByName(name);
    }
}
