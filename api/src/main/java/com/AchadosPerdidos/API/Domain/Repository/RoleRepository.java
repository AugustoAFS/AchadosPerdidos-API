package com.AchadosPerdidos.API.Domain.Repository;

import com.AchadosPerdidos.API.Domain.Entity.Role;
import com.AchadosPerdidos.API.Domain.Interfaces.IRoleRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<Role, Integer>, IRoleRepository {

    @Override
    Optional<Role> findByNameAndActiveTrue(String name);
}
