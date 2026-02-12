package com.example.demo.Model.DTOS.Mappers;

import com.example.demo.Model.DTOS.Request.CartCreateRequest;
import com.example.demo.Model.DTOS.Response.CartWithItemsResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.Model.Entities.CartEntity;

import com.example.demo.Model.DTOS.Response.CartResponse;

@Mapper(componentModel = "spring", uses = CustomerDataMapper.class)
public interface CartMapper {

    @Mapping(source = "user.id", target = "userId")
    CartResponse toResponse(CartEntity entity);

    @Mapping(source = "user.id", target = "userId")
    CartWithItemsResponse toResponseWithItems(CartEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "items", ignore = true)
    @Mapping(target = "total", ignore = true)
    @Mapping(target = "cartStatus", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    CartEntity toEntity(CartCreateRequest request);
}
