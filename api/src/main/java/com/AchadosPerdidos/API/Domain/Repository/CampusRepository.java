package com.AchadosPerdidos.API.Domain.Repository;

import com.AchadosPerdidos.API.Domain.Entity.Campus;
import com.AchadosPerdidos.API.Domain.Interfaces.ICampusRepository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampusRepository extends BaseRepository<Campus, Integer>, ICampusRepository {

    @Override
    List<Campus> findByActiveTrue();

    @Override
    List<Campus> findByInstitutionIdAndActiveTrue(Integer institutionId);

    @Override
    boolean existsByNameAndInstitutionId(String name, Integer institutionId);
}
