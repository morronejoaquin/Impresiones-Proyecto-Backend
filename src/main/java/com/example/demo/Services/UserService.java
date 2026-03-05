package com.example.demo.Services;

import com.example.demo.Exceptions.BusinessException;
import com.example.demo.Model.DTOS.Mappers.UserMapper;
import com.example.demo.Model.DTOS.Request.UpdateProfileRequest;
import com.example.demo.Model.DTOS.Response.UserResponse;
import com.example.demo.Model.DTOS.Response.ProfileResponse;
import com.example.demo.Model.Entities.UserEntity;
import com.example.demo.Model.Enums.ErrorCode;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Security.Model.Entities.CredentialsEntity;
import com.example.demo.Security.Repositories.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
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
            CredentialsRepository credentialsRepository
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
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return userMapper.toResponse(entity);
    }

    public void update(UUID id, Map<String, Object> camposActualizados) {
        UserEntity entity = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

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
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        CredentialsEntity credentials = credentialsRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.CREDENTIALS_NOT_FOUND));

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
                .role(role)
                .build();
    }

    public UserResponse updateProfile(
            UpdateProfileRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setPhone(request.getPhone());

        userRepository.save(user);

        return userMapper.toResponse(user);
    }
}
