package com.AchadosPerdidos.API.Domain.Repository;

import com.AchadosPerdidos.API.Domain.Entity.Users;
import com.AchadosPerdidos.API.Domain.Interfaces.IUsersRepository;

import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends BaseRepository<Users, Integer>, IUsersRepository {

    @Override
    Optional<Users> findByEmailAndActiveTrue(String email);

    @Override
    boolean existsByEmail(String email);
}
