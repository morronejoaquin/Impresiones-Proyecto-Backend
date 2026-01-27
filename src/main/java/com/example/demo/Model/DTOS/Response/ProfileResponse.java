package com.example.demo.Model.DTOS.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private UUID id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String password;
    private String role;
}
