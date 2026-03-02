package com.AchadosPerdidos.API.Domain.Repository;

import com.AchadosPerdidos.API.Domain.Entity.Users;
import com.AchadosPerdidos.API.Domain.Interfaces.IUsersRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsersRepository extends BaseRepository<Users, Integer>, IUsersRepository {

    @Override
    Optional<Users> findByEmailAndActiveTrue(String email);

    @Override
    boolean existsByEmail(String email);

    /**
     * Retorna os device tokens (OneSignal Player ID) de todos os usuários
     * ativos no campus informado que tenham token registrado.
     * Usado para enviar push notification de novo item para o campus.
     */
    @Query("""
            SELECT u.deviceToken FROM Users u
            JOIN User_Campus uc ON uc.userId = u.id
            WHERE uc.campusId = :campusId
              AND uc.active = true
              AND u.active = true
              AND u.deviceToken IS NOT NULL
              AND u.deviceToken <> ''
              AND u.id <> :excludeUserId
            """)
    List<String> findDeviceTokensByCampusId(
            @Param("campusId") Integer campusId,
            @Param("excludeUserId") Integer excludeUserId);

    /**
     * Retorna o device token de um usuário específico.
     * Usado para notificações diretas (mensagem de chat, por ex).
     */
    @Query("SELECT u.deviceToken FROM Users u WHERE u.id = :userId AND u.deviceToken IS NOT NULL")
    Optional<String> findDeviceTokenByUserId(@Param("userId") Integer userId);
}
