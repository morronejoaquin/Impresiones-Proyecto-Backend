package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Response.UserResponse;
import com.example.demo.Model.DTOS.Response.ProfileResponse;
import com.example.demo.Services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAll(Pageable pageable){
        Page<UserResponse> users = service.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable UUID id){
        UserResponse user = service.findById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping
    public ResponseEntity<String> update(@PathVariable UUID id, @RequestBody Map<String, Object> camposActualizados){
        service.update(id, camposActualizados);
        return ResponseEntity.ok("Usuario actualizado correctamente");
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = authentication.getName();
        ProfileResponse profile = service.getProfile(email);
        return ResponseEntity.ok(profile);
    }
}
