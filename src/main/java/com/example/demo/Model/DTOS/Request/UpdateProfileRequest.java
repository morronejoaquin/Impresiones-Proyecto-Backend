package com.example.demo.Model.DTOS.Request;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String name;
    private String surname;
    private String phone;
}
