package com.example.demo.Security.Services;

import com.example.demo.Exceptions.InvalidCredentialsException;
import com.example.demo.Model.DTOS.Mappers.UserMapper;
import com.example.demo.Model.DTOS.Response.UserResponse;
import com.example.demo.Model.Entities.UserEntity;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Security.DTOs.AuthResponse;
import com.example.demo.Security.DTOs.LoginRequest;
import com.example.demo.Security.DTOs.RegisterRequest;
import com.example.demo.Security.DTOs.RegisterResponse;
import com.example.demo.Security.Model.Entities.CredentialsEntity;
import com.example.demo.Security.Model.Entities.RoleEntity;
import com.example.demo.Security.Model.Enums.Rol;
import com.example.demo.Security.Repositories.CredentialsRepository;
import com.example.demo.Security.Repositories.RoleRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private final CredentialsRepository credentialsRepository;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    public AuthService(CredentialsRepository credentialsRepository, AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtService jwtService, UserMapper userMapper) {
        this.credentialsRepository = credentialsRepository;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Email o contraseña incorrectos");
        }

        CredentialsEntity credentials = credentialsRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        String token = jwtService.generateToken(credentials);
        return new AuthResponse(token);
    }

    public UserResponse me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No hay una sesión activa");
        }

        String email = authentication.getName();

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toResponse(user);
    }

    public RegisterResponse register(RegisterRequest registerRequest) {
        // Validar que el email no esté registrado
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        if (credentialsRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado en el sistema");
        }

        UserEntity user = new UserEntity();
        user.setName(registerRequest.getName());
        user.setSurname(registerRequest.getSurname());
        user.setEmail(registerRequest.getEmail());
        user.setPhone(registerRequest.getPhone());
        
        UserEntity savedUser = userRepository.save(user);

        RoleEntity clienteRole = roleRepository.findByRole(Rol.cliente)
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado en el sistema"));

        CredentialsEntity credentials = new CredentialsEntity();
        credentials.setEmail(registerRequest.getEmail());
        credentials.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        credentials.setUser(savedUser);
        credentials.setRoles(Set.of(clienteRole));

        CredentialsEntity savedCredentials = credentialsRepository.save(credentials);

        String token = jwtService.generateToken(savedCredentials);

        return RegisterResponse.builder()
                .message("Usuario registrado exitosamente")
                .email(savedUser.getEmail())
                .token(token)
                .build();
    }
}