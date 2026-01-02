package DTOS.Mappers;

import org.mapstruct.*;

import com.example.demo.Entities.UserEntity;

import DTOS.Request.UserCreateRequest;
import DTOS.Response.UserResponse;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toEntity(UserCreateRequest req);
    UserResponse toResponse(UserEntity entity);
}

