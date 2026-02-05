package com.example.demo.Model.DTOS.Request;

import com.example.demo.Model.Enums.BindingTypeEnum;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderItemCreateRequest {

    private boolean color;
    private boolean doubleSided;

    private BindingTypeEnum binding;

    @Size(max = 500, message = "El comentario no puede superar los 500 caracteres")
    private String comments;

    @Min(value = 1, message = "La cantidad de copias debe ser al menos 1")
    private int copies;

}

