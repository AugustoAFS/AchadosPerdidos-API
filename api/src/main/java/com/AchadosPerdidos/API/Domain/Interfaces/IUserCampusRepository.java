package com.AchadosPerdidos.API.Domain.Interfaces;

import com.AchadosPerdidos.API.Domain.Entity.User_Campus;

import java.util.List;

public interface IUserCampusRepository {

    List<User_Campus> findByUserIdAndActiveTrue(Integer userId);

    List<User_Campus> findByCampusIdAndActiveTrue(Integer campusId);

    boolean existsByUserIdAndCampusId(Integer userId, Integer campusId);
}
