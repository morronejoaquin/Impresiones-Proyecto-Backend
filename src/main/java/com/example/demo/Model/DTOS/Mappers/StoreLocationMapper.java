package com.example.demo.Model.DTOS.Mappers;

import com.example.demo.Model.DTOS.Response.StoreLocationResponse;
import com.example.demo.Model.Entities.StoreLocationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface StoreLocationMapper {

    @Mappings({
            @Mapping(source = "id", target = "id"),
            @Mapping(source = "lat", target = "lat"),
            @Mapping(source = "lng", target = "lng"),
            @Mapping(source = "address", target = "address"),
    })    StoreLocationResponse toResponse(StoreLocationEntity entity);
}
