package com.example.demo.Model.DTOS.Mappers;

import com.example.demo.Model.DTOS.Request.PricesUpdateRequest;
import org.mapstruct.Mapper;

import com.example.demo.Model.Entities.PricesEntity;

import com.example.demo.Model.DTOS.Response.PricesResponse;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PricesMapper {

    @Mapping(target = "id", ignore = true)
    PricesEntity toEntity(PricesUpdateRequest req);

    PricesResponse toResponse(PricesEntity entity);
}
