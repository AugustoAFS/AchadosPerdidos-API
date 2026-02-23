package com.AchadosPerdidos.API.Application.Services;

import com.AchadosPerdidos.API.Application.Interfaces.ICampusService;
import com.AchadosPerdidos.API.Domain.Entity.Campus;
import com.AchadosPerdidos.API.Domain.Repository.CampusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CampusService extends BaseService<Campus, Integer, CampusRepository>
        implements ICampusService {

    public CampusService(CampusRepository repository) {
        super(repository);
    }

    @Override
    @Transactional
    public Campus create(Campus campus) {
        if (repository.existsByNameAndInstitutionId(campus.getName(), campus.getInstitutionId())) {
            throw new IllegalArgumentException("Campus já existe com este nome nesta instituição.");
        }
        return repository.save(campus);
    }

    @Override
    @Transactional
    public Campus update(Integer id, Campus data) {
        Campus existing = findById(id);
        existing.setName(data.getName());
        return repository.save(existing);
    }

    @Override
    @Transactional
    public void deactivate(Integer id) {
        Campus campus = findById(id);
        campus.setActive(false);
        campus.setDeletedAt(LocalDateTime.now());
        repository.save(campus);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Campus> findByInstitution(Integer institutionId) {
        return repository.findByInstitutionIdAndActiveTrue(institutionId);
    }
}
