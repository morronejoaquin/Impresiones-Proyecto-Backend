package DTOS.Mappers;

import org.mapstruct.*;

import com.example.demo.Entities.CustomerDataEntity;

import DTOS.Request.CustomerDataRequest;
import DTOS.Response.CustomerDataResponse;

@Mapper(componentModel = "spring")
public interface CustomerDataMapper {
    CustomerDataEntity toEntity(CustomerDataRequest req);
    CustomerDataResponse toResponse(CustomerDataEntity entity);
}

