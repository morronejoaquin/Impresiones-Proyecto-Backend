package com.example.demo.Model.DTOS.Mappers;


import com.example.demo.Model.Entities.UserEntity;

import org.mapstruct.Mapper;

import com.example.demo.Model.DTOS.Request.UserCreateRequest;
import com.example.demo.Model.DTOS.Response.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserCreateRequest req);

    UserResponse toResponse(UserEntity entity);
}

