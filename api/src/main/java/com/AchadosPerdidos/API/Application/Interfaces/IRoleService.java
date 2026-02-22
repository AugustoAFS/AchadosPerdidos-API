package com.AchadosPerdidos.API.Application.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Role;

import java.util.List;

public interface IRoleService {

    Role findById(Integer id);

    Role findByName(String name);

    List<Role> findAll();
}
