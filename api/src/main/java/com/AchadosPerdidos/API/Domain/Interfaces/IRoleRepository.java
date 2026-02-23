package com.AchadosPerdidos.API.Domain.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Role;

import java.util.Optional;

public interface IRoleRepository {

    Optional<Role> findByNameAndActiveTrue(String name);
}
