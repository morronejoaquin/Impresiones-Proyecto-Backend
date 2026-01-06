package com.example.demo.Model.DTOS.Response;

import java.util.UUID;

import com.example.demo.Model.Enums.UserRoleEnum;

import lombok.Data;

@Data
public class UserResponse {
    private UUID id;
    private String username;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private UserRoleEnum role;
}

