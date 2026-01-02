package DTOS.Mappers;

import org.mapstruct.*;

import com.example.demo.Entities.OrderItemEntity;

import DTOS.Request.OrderItemCreateRequest;
import DTOS.Response.OrderItemResponse;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "cart.id", target = "cartId")
    OrderItemResponse toResponse(OrderItemEntity entity);

    @Mapping(source = "cartId", target = "cart.id")
    OrderItemEntity toEntity(OrderItemCreateRequest req);
}

