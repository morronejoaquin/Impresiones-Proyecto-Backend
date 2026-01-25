package com.example.demo.Model.DTOS.Mappers;

import org.mapstruct.Mapper;

import com.example.demo.Model.Entities.CustomerDataEntity;

import com.example.demo.Model.DTOS.Request.CustomerDataRequest;
import com.example.demo.Model.DTOS.Response.CustomerDataResponse;

@Mapper(componentModel = "spring")
public interface CustomerDataMapper {
    CustomerDataEntity toEntity(CustomerDataRequest req);
    CustomerDataResponse toResponse(CustomerDataEntity entity);
}

