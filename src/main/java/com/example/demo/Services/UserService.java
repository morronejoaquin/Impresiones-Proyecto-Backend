package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.UserMapper;
import com.example.demo.Model.DTOS.Request.UserCreateRequest;
import com.example.demo.Model.DTOS.Response.UserResponse;
import com.example.demo.Model.Entities.OrderItemEntity;
import com.example.demo.Model.Entities.UserEntity;
import com.example.demo.Repositories.UserRepository;
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
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(
            UserMapper userMapper,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void save(UserCreateRequest request){
    UserEntity entity = userMapper.toEntity(request);

    entity.setPassword(
        passwordEncoder.encode(request.getPassword())
    );
    userRepository.save(entity);
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

        if (key.equals("password")) {
            entity.setPassword(
                passwordEncoder.encode(value.toString())
            );
            return;
        }

        Field campo = ReflectionUtils.findField(UserEntity.class, key);
        if (campo != null) {
            campo.setAccessible(true);
            ReflectionUtils.setField(campo, entity, value);
        }
    });

    userRepository.save(entity);
}

}
