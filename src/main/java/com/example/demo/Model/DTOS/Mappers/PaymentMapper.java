package com.example.demo.Model.DTOS.Mappers;

import org.mapstruct.*;

import com.example.demo.Model.Entities.PaymentEntity;

import com.example.demo.Model.DTOS.Request.PaymentCreateRequest;
import com.example.demo.Model.DTOS.Response.PaymentResponse;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "cart.id", target = "cartId")
    PaymentResponse toResponse(PaymentEntity entity);

    PaymentEntity toEntity(PaymentCreateRequest req);
}

