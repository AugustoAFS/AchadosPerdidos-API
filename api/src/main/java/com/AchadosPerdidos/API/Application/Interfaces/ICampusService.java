package com.AchadosPerdidos.API.Application.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Campus;

import java.util.List;

public interface ICampusService extends IBaseService<Campus, Integer> {

    List<Campus> findByInstitution(Integer institutionId);
}
