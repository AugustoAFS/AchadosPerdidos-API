package com.AchadosPerdidos.API.Domain.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Institution;

import java.util.List;
import java.util.Optional;

public interface IInstitutionRepository {

    Optional<Institution> findByCnpjAndActiveTrue(String cnpj);

    List<Institution> findByPrefixAndActiveTrue(String prefix);

    boolean existsByCnpj(String cnpj);
}
