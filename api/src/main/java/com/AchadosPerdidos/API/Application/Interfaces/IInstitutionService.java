package com.AchadosPerdidos.API.Application.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Institution;

public interface IInstitutionService extends IBaseService<Institution, Integer> {

    boolean existsByCnpj(String cnpj);
}
