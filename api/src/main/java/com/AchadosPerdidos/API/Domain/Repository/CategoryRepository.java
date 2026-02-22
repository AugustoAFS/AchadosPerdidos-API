package com.AchadosPerdidos.API.Domain.Repository;

import com.AchadosPerdidos.API.Domain.Entity.Category;
import com.AchadosPerdidos.API.Domain.Interfaces.ICategoryRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends BaseRepository<Category, Integer>, ICategoryRepository {

    @Override
    Optional<Category> findByNameAndActiveTrue(String name);

    @Override
    boolean existsByName(String name);
}
