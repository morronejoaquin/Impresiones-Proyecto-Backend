package com.example.demo.Model.DTOS.Response;

import java.util.UUID;

import com.example.demo.Model.Enums.BindingTypeEnum;

import com.example.demo.Model.Enums.FileTypeEnum;
import lombok.Data;

@Data
public class OrderItemResponse {
    private UUID id;
    private UUID cartId;
    private boolean isColor;
    private boolean isDoubleSided;
    private BindingTypeEnum binding;
    private int pages;
    private String comments;
    private String file;
    private FileTypeEnum fileType;
    private int copies;
    private double amount;
    private Integer imageWidth;
    private Integer imageHeight;
    private boolean deleted = false;
}
