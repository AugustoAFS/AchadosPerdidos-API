package com.AchadosPerdidos.API.Domain.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Category;

import java.util.Optional;

public interface ICategoryRepository {

    Optional<Category> findByNameAndActiveTrue(String name);

    boolean existsByName(String name);
}
