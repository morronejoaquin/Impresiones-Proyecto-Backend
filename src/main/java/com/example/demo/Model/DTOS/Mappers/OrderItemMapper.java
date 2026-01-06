package com.example.demo.Model.DTOS.Mappers;

import org.mapstruct.*;

import com.example.demo.Model.Entities.OrderItemEntity;

import com.example.demo.Model.DTOS.Request.OrderItemCreateRequest;
import com.example.demo.Model.DTOS.Response.OrderItemResponse;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "cart.id", target = "cartId")
    OrderItemResponse toResponse(OrderItemEntity entity);

    @Mapping(target = "cart", ignore = true)
    OrderItemEntity toEntity(OrderItemCreateRequest req);
}

