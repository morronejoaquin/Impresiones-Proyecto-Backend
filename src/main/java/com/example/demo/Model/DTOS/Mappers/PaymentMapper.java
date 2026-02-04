package com.example.demo.Model.DTOS.Mappers;

import com.example.demo.Model.DTOS.Response.PaymentHistoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.demo.Model.Entities.PaymentEntity;

import com.example.demo.Model.DTOS.Request.PaymentCreateRequest;
import com.example.demo.Model.DTOS.Response.PaymentResponse;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "cart.id", target = "cartId")
    PaymentResponse toResponse(PaymentEntity entity);

    PaymentEntity toEntity(PaymentCreateRequest req);

    @Mapping(source = "cart.id", target = "cartId")
    PaymentHistoryResponse toHistoryResponse(PaymentEntity entity);
}

