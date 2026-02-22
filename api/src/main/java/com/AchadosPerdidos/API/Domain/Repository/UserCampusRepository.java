package com.AchadosPerdidos.API.Domain.Repository;

import com.AchadosPerdidos.API.Domain.Entity.User_Campus;
import com.AchadosPerdidos.API.Domain.Interfaces.IUserCampusRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCampusRepository extends BaseRepository<User_Campus, Integer>, IUserCampusRepository {

    @Override
    List<User_Campus> findByUserIdAndActiveTrue(Integer userId);

    @Override
    List<User_Campus> findByCampusIdAndActiveTrue(Integer campusId);

    @Override
    boolean existsByUserIdAndCampusId(Integer userId, Integer campusId);
}
