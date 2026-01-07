package com.example.demo.Controllers;

import com.example.demo.Model.DTOS.Request.CartCreateRequest;
import com.example.demo.Model.DTOS.Request.UserCreateRequest;
import com.example.demo.Model.DTOS.Response.CartResponse;
import com.example.demo.Model.DTOS.Response.UserResponse;
import com.example.demo.Services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<String> save(UserCreateRequest request){
        service.save(request);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario creado correctamente");
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> getAll(Pageable pageable){
        Page<UserResponse> users = service.findAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(UUID id){
        UserResponse user = service.findById(id);
        return ResponseEntity.ok(user);
    }

    @PatchMapping
    public ResponseEntity<String> update(UUID id, Map<String, Object> camposActualizados){
        service.update(id, camposActualizados);
        return ResponseEntity.ok("Usuario actualizado correctamente");
    }
}
