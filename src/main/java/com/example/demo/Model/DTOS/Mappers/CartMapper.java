package com.example.demo.Model.DTOS.Mappers;

import org.mapstruct.*;

import com.example.demo.Model.Entities.CartEntity;

import com.example.demo.Model.DTOS.Request.CartCreateRequest;
import com.example.demo.Model.DTOS.Response.CartResponse;

@Mapper(componentModel = "spring", uses = CustomerDataMapper.class)
public interface CartMapper {

    @Mapping(source = "user.id", target = "userId")
    CartResponse toResponse(CartEntity entity);

    CartEntity toEntity(CartCreateRequest req);
}
