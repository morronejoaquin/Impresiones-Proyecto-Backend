package DTOS.Mappers;

import org.mapstruct.Mapper;

import com.example.demo.Entities.PricesEntity;

import DTOS.Response.PricesResponse;

@Mapper(componentModel = "spring")
public interface PricesMapper {
    PricesResponse toResponse(PricesEntity entity);
}
