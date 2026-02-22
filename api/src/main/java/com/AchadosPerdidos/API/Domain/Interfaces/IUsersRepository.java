package com.AchadosPerdidos.API.Domain.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.Users;

import java.util.Optional;

public interface IUsersRepository {

    Optional<Users> findByEmailAndActiveTrue(String email);

    boolean existsByEmail(String email);
}
