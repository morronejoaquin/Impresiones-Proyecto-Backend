package com.example.demo.Security.Controller;
import com.example.demo.Model.DTOS.Response.UserResponse;
import com.example.demo.Security.Services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.demo.Security.DTOs.AuthResponse;
import com.example.demo.Security.DTOs.LoginRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication){
        UserResponse response = authService.me(authentication);
        return ResponseEntity.ok(response);
    }

}
