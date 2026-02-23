package com.AchadosPerdidos.API.Application.Services;

import com.AchadosPerdidos.API.Application.Interfaces.IInstitutionService;
import com.AchadosPerdidos.API.Domain.Entity.Institution;
import com.AchadosPerdidos.API.Domain.Repository.InstitutionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InstitutionService extends BaseService<Institution, Integer, InstitutionRepository>
        implements IInstitutionService {

    public InstitutionService(InstitutionRepository repository) {
        super(repository);
    }

    @Override
    @Transactional
    public Institution create(Institution institution) {
        if (repository.existsByCnpj(institution.getCnpj())) {
            throw new IllegalArgumentException("Instituição já cadastrada com CNPJ: " + institution.getCnpj());
        }
        return repository.save(institution);
    }

    @Override
    @Transactional
    public Institution update(Integer id, Institution data) {
        Institution existing = findById(id);
        existing.setName(data.getName());
        existing.setPrefix(data.getPrefix());
        return repository.save(existing);
    }

    @Override
    @Transactional
    public void deactivate(Integer id) {
        Institution institution = findById(id);
        institution.setActive(false);
        institution.setDeletedAt(LocalDateTime.now());
        repository.save(institution);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCnpj(String cnpj) {
        return repository.existsByCnpj(cnpj);
    }
}
