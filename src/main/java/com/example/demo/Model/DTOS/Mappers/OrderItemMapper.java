package com.example.demo.Model.DTOS.Mappers;

import org.mapstruct.*;

import com.example.demo.Model.Entities.OrderItemEntity;

import com.example.demo.Model.DTOS.Request.OrderItemCreateRequest;
import com.example.demo.Model.DTOS.Response.OrderItemResponse;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "cart.id", target = "cartId"),
            @Mapping(source = "color", target = "color"),
            @Mapping(source = "doubleSided", target = "doubleSided"),
            @Mapping(source = "binding", target = "binding"),
            @Mapping(source = "pages", target = "pages"),
            @Mapping(source = "comments", target = "comments"),
            @Mapping(source = "driveFileId", target = "driveFileId"),
            @Mapping(source = "fileName", target = "fileName"),
            @Mapping(source = "fileType", target = "fileType"),
            @Mapping(source = "copies", target = "copies"),
            @Mapping(source = "amount", target = "amount"),
            @Mapping(source = "imageWidth", target = "imageWidth"),
            @Mapping(source = "imageHeight", target = "imageHeight"),
            @Mapping(source = "deleted", target = "deleted")
    })
    OrderItemResponse toResponse(OrderItemEntity entity);

    @Mapping(target = "cart", ignore = true)
    OrderItemEntity toEntity(OrderItemCreateRequest req);
}

