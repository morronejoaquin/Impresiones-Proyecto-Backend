package com.example.demo.Services;

import com.example.demo.Model.DTOS.Mappers.UserMapper;
import com.example.demo.Model.DTOS.Request.UserCreateRequest;
import com.example.demo.Model.DTOS.Response.UserResponse;
import com.example.demo.Model.Entities.UserEntity;
import com.example.demo.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserMapper userMapper, UserRepository userRepository) {
        this.userMapper = userMapper;
        this.userRepository = userRepository;
    }

    public void save(UserCreateRequest request){
        UserEntity entity = userMapper.toEntity(request);
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
}
