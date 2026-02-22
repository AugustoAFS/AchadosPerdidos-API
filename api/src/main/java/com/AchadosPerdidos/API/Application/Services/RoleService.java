package com.AchadosPerdidos.API.Application.Services;

import com.AchadosPerdidos.API.Application.Interfaces.IRoleService;
import com.AchadosPerdidos.API.Domain.Entity.Role;
import com.AchadosPerdidos.API.Domain.Repository.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleService extends BaseService<Role, Integer, RoleRepository>
        implements IRoleService {

    public RoleService(RoleRepository repository) {
        super(repository);
    }

    @Override
    public Role update(Integer id, Role data) {
        throw new UnsupportedOperationException("Roles não são atualizadas via API.");
    }

    @Override
    public void deactivate(Integer id) {
        throw new UnsupportedOperationException("Roles não são desativadas via API.");
    }

    @Override
    @Transactional(readOnly = true)
    public Role findByName(String name) {
        return repository.findByNameAndActiveTrue(name)
                .orElseThrow(() -> new EntityNotFoundException("Role não encontrada: " + name));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return repository.findByActiveTrue();
    }
}
