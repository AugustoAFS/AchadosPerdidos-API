package com.AchadosPerdidos.API.Application.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Category;

public interface ICategoryService extends IBaseService<Category, Integer> {

    boolean existsByName(String name);
}
