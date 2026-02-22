package com.AchadosPerdidos.API.Domain.Repository;

import com.AchadosPerdidos.API.Domain.Entity.Institution;
import com.AchadosPerdidos.API.Domain.Interfaces.IInstitutionRepository;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InstitutionRepository extends BaseRepository<Institution, Integer>, IInstitutionRepository {

    @Override
    Optional<Institution> findByCnpjAndActiveTrue(String cnpj);

    @Override
    List<Institution> findByPrefixAndActiveTrue(String prefix);

    @Override
    boolean existsByCnpj(String cnpj);
}
