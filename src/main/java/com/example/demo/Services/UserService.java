package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.UserMapper;
import com.example.demo.Model.DTOS.Request.UserCreateRequest;
import com.example.demo.Model.DTOS.Response.UserResponse;
import com.example.demo.Model.DTOS.Response.ProfileResponse;
import com.example.demo.Model.Entities.OrderItemEntity;
import com.example.demo.Model.Entities.UserEntity;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Security.Model.Entities.CredentialsEntity;
import com.example.demo.Security.Repositories.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final CredentialsRepository credentialsRepository;

    @Autowired
    public UserService(
            UserMapper userMapper,
            UserRepository userRepository,
            CredentialsRepository credentialsRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
    }

    public Page<UserResponse> findAll(Pageable pageable){
        Page<UserEntity> page = userRepository.findAll(pageable);
        return page.map(userMapper::toResponse);
    }

    public UserResponse findById(UUID id){
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        return userMapper.toResponse(entity);
    }

    public void update(UUID id, Map<String, Object> camposActualizados) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        if (camposActualizados.containsKey("id")) {
            throw new IllegalArgumentException("No está permitido modificar el campo 'id'");
        }

        camposActualizados.forEach((key, value) -> {

            if (key.equals("password")) return;

            Field campo = ReflectionUtils.findField(UserEntity.class, key);
            if (campo != null) {
                campo.setAccessible(true);
                ReflectionUtils.setField(campo, entity, value);
            }
        });

        userRepository.save(entity);
    }

    public ProfileResponse getProfile(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        CredentialsEntity credentials = credentialsRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Credenciales del usuario no encontradas"));

        String role = credentials.getRoles().stream()
                .map(roleEntity -> roleEntity.getRole().name())
                .findFirst()
                .orElse("UNKNOWN");

        return ProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .password(credentials.getPassword())
                .role(role)
                .build();
    }
}
