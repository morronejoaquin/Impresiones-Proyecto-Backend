package com.example.demo.Model.DTOS.Mappers;

import org.mapstruct.*;

import com.example.demo.Model.Entities.UserEntity;

import com.example.demo.Model.DTOS.Request.UserCreateRequest;
import com.example.demo.Model.DTOS.Response.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserCreateRequest req);

    UserResponse toResponse(UserEntity entity);
}

