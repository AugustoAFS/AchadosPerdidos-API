package com.AchadosPerdidos.API.Application.Interfaces;

import java.util.List;

public interface IBaseService<T, ID> {

    T create(T entity);

    T findById(ID id);

    List<T> findAll();

    T update(ID id, T data);

    void deactivate(ID id);
}
