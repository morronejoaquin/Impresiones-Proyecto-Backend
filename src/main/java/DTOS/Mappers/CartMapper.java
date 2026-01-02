package DTOS.Mappers;

import org.mapstruct.*;

import com.example.demo.Entities.CartEntity;

import DTOS.Request.CartCreateRequest;
import DTOS.Response.CartResponse;

@Mapper(componentModel = "spring", uses = CustomerDataMapper.class)
public interface CartMapper {

    @Mapping(source = "user.id", target = "userId")
    CartResponse toResponse(CartEntity entity);

    @Mapping(source = "userId", target = "user.id")
    CartEntity toEntity(CartCreateRequest req);
}
