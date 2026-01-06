package com.example.demo.Model.DTOS.Response;

import java.util.UUID;

import com.example.demo.Model.Enums.BindingTypeEnum;

import lombok.Data;

@Data
public class OrderItemResponse {
    private UUID id;
    private UUID cartId;
    private int pages;
    private int copies;
    private double amount;
    private boolean isColor;
    private boolean isDoubleSided;
    private BindingTypeEnum binding;
    private String comments;
}
