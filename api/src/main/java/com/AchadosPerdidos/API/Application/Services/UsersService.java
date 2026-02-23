package com.AchadosPerdidos.API.Application.Services;

import com.AchadosPerdidos.API.Application.DTOs.Request.User.LoginRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Request.User.RedefinirSenhaRequestDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Auth.OAuthUserDTO;
import com.AchadosPerdidos.API.Application.DTOs.Response.Auth.TokenResponseDTO;
import com.AchadosPerdidos.API.Application.Interfaces.IUsersService;
import com.AchadosPerdidos.API.Application.Interfaces.Auth.IJWTService;
import com.AchadosPerdidos.API.Application.Interfaces.Auth.IOAuthProviderService;
import com.AchadosPerdidos.API.Domain.Entity.User_Campus;
import com.AchadosPerdidos.API.Domain.Entity.Users;
import com.AchadosPerdidos.API.Domain.Enum.Role_Type;
import com.AchadosPerdidos.API.Domain.Repository.RoleRepository;
import com.AchadosPerdidos.API.Domain.Repository.UserCampusRepository;
import com.AchadosPerdidos.API.Domain.Repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService extends BaseService<Users, Integer, UsersRepository>
        implements IUsersService {

    private static final Logger log = LoggerFactory.getLogger(UsersService.class);

    @Autowired
    private UserCampusRepository userCampusRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private IJWTService jwtService;

    public UsersService(UsersRepository repository) {
        super(repository);
    }

    @Override
    @Transactional
    public Users create(Users user) {
        if (repository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado: " + user.getEmail());
        }
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        log.info("Criando usuário: {}", user.getEmail());
        return repository.save(user);
    }

    @Transactional
    public Users createWithCampuses(Users user, List<Integer> campusIds) {
        Users saved = create(user);
        if (campusIds != null) {
            for (Integer campusId : campusIds) {
                User_Campus link = new User_Campus();
                link.setUserId(saved.getId());
                link.setCampusId(campusId);
                userCampusRepository.save(link);
            }
        }
        return saved;
    }

    @Override
    @Transactional
    public Users update(Integer id, Users data) {
        Users existing = findById(id);
        existing.setName(data.getName());
        existing.setBirthDate(data.getBirthDate());
        log.info("Atualizando usuário id={}", id);
        return repository.save(existing);
    }

    @Transactional
    public Users updateWithCampuses(Integer id, Users data, List<Integer> campusIds) {
        Users updated = update(id, data);
        if (campusIds != null) {
            List<User_Campus> currentLinks = userCampusRepository.findByUserIdAndActiveTrue(id);
            for (User_Campus link : currentLinks) {
                link.setActive(false);
                link.setDeletedAt(LocalDateTime.now());
                userCampusRepository.save(link);
            }
            for (Integer campusId : campusIds) {
                User_Campus link = new User_Campus();
                link.setUserId(id);
                link.setCampusId(campusId);
                userCampusRepository.save(link);
            }
        }
        return updated;
    }

    @Override
    @Transactional
    public void deactivate(Integer id) {
        Users user = findById(id);
        user.setActive(false);
        user.setDeletedAt(LocalDateTime.now());
        repository.save(user);

        List<User_Campus> links = userCampusRepository.findByUserIdAndActiveTrue(id);
        for (User_Campus link : links) {
            link.setActive(false);
            link.setDeletedAt(LocalDateTime.now());
            userCampusRepository.save(link);
        }
        log.info("Usuário desativado id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Users> findByEmail(String email) {
        return repository.findByEmailAndActiveTrue(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Users> findByCampus(Integer campusId) {
        return userCampusRepository.findByCampusIdAndActiveTrue(campusId).stream()
                .map(uc -> findById(uc.getUserId()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Integer> getCampusIdsForUser(Integer userId) {
        return userCampusRepository.findByUserIdAndActiveTrue(userId).stream()
                .map(User_Campus::getCampusId)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    @Override
    @Transactional
    public TokenResponseDTO login(LoginRequestDTO dto) {
        Users user = repository.findByEmailAndActiveTrue(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("E-mail ou senha inválidos"));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("E-mail ou senha inválidos");
        }

        String roleName = (user.getRoleId() != null)
                ? roleRepository.findById(user.getRoleId()).map(role -> role.getName()).orElse(Role_Type.ALUNO.name())
                : Role_Type.ALUNO.name();

        String token = jwtService.createToken(
                user.getEmail(),
                user.getName(),
                roleName,
                user.getId().toString());

        TokenResponseDTO response = new TokenResponseDTO();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setRole(roleName);
        response.setExpiresInMinutes(60);

        log.info("Login bem-sucedido para: {}", dto.getEmail());
        return response;
    }

    @Override
    @Transactional
    public void redefinirSenha(RedefinirSenhaRequestDTO dto) {
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("As senhas não coincidem");
        }

        Users user = repository.findByEmailAndActiveTrue(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado com o e-mail informado"));

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        repository.save(user);
        log.info("Senha redefinida para: {}", dto.getEmail());
    }

    @Override
    @Transactional
    public TokenResponseDTO loginWithGoogle(String code, IOAuthProviderService oAuthProvider, IJWTService jwtService) {
        OAuthUserDTO oauthUser = oAuthProvider.exchangeCodeForUserInfo(code);

        Users user = repository.findByEmailAndActiveTrue(oauthUser.getEmail())
                .orElseGet(() -> {
                    Users newUser = new Users();
                    newUser.setName(oauthUser.getName());
                    newUser.setEmail(oauthUser.getEmail());
                    newUser.setPasswordHash(passwordEncoder.encode(java.util.UUID.randomUUID().toString()));
                    newUser.setRoleId(3);
                    return repository.save(newUser);
                });

        String roleName = (user.getRoleId() != null)
                ? roleRepository.findById(user.getRoleId()).map(role -> role.getName()).orElse(Role_Type.ALUNO.name())
                : Role_Type.ALUNO.name();

        String token = jwtService.createToken(
                user.getEmail(),
                user.getName(),
                roleName,
                user.getId().toString());

        TokenResponseDTO response = new TokenResponseDTO();
        response.setToken(token);
        response.setEmail(user.getEmail());
        response.setRole(roleName);
        response.setExpiresInMinutes(60);

        log.info("Login com Google bem-sucedido para: {}", user.getEmail());
        return response;
    }
}
