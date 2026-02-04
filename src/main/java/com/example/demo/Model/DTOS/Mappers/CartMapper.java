package com.example.demo.Model.DTOS.Mappers;

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
}
