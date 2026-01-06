package com.example.demo.Model.DTOS.Request;

import lombok.Data;

@Data
public class UserCreateRequest {
    private String username;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String password;
}
