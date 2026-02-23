package com.AchadosPerdidos.API.Domain.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Campus;

import java.util.List;

public interface ICampusRepository {

    List<Campus> findByActiveTrue();

    List<Campus> findByInstitutionIdAndActiveTrue(Integer institutionId);

    boolean existsByNameAndInstitutionId(String name, Integer institutionId);
}
