package com.example.demo.Model.DTOS.Request;

import com.example.demo.Model.Enums.BindingTypeEnum;

import lombok.Data;

@Data
public class OrderItemCreateRequest {
    private boolean isColor;
    private boolean isDoubleSided;
    private BindingTypeEnum binding;
    private int pages;
    private String comments;
    private String file;
    private int copies;
    private double amount;
    private Integer imageWidth;
    private Integer imageHeight;

}

