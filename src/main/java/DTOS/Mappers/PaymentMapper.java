package DTOS.Mappers;

import org.mapstruct.*;

import com.example.demo.Entities.PaymentEntity;

import DTOS.Request.PaymentCreateRequest;
import DTOS.Response.PaymentResponse;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "cart.id", target = "cartId")
    PaymentResponse toResponse(PaymentEntity entity);

    @Mapping(source = "cartId", target = "cart.id")
    PaymentEntity toEntity(PaymentCreateRequest req);
}

