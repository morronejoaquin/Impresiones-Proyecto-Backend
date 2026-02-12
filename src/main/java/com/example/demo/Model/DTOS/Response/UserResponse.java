package com.example.demo.Model.DTOS.Response;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
public class UserResponse {
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String role;
}

