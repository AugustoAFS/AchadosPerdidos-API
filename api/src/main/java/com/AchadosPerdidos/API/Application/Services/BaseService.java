package com.AchadosPerdidos.API.Application.Services;

import com.AchadosPerdidos.API.Application.Interfaces.IBaseService;
import com.AchadosPerdidos.API.Domain.Repository.BaseRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public abstract class BaseService<T, ID, R extends BaseRepository<T, ID>>
        implements IBaseService<T, ID> {

    protected final R repository;

    protected BaseService(R repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public T create(T entity) {
        return repository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public T findById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entidade não encontrada com id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> findAll() {
        return repository.findByActiveTrue();
    }

    @Override
    @Transactional
    public abstract T update(ID id, T data);

    @Override
    @Transactional
    public abstract void deactivate(ID id);
}
