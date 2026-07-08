package com.example.demo.Security.Services;

import com.example.demo.Exceptions.InvalidCredentialsException;
import com.example.demo.Model.DTOS.Mappers.UserMapper;
import com.example.demo.Model.Entities.UserEntity;
import com.example.demo.Repositories.UserRepository;
import com.example.demo.Security.DTOs.AuthResponse;
import com.example.demo.Security.DTOs.LoginRequest;
import com.example.demo.Security.DTOs.RegisterRequest;
import com.example.demo.Security.DTOs.RegisterResponse;
import com.example.demo.Security.Model.Entities.CredentialsEntity;
import com.example.demo.Security.Model.Entities.RefreshToken;
import com.example.demo.Security.Model.Entities.RoleEntity;
import com.example.demo.Security.Model.Enums.Rol;
import com.example.demo.Security.Repositories.CredentialsRepository;
import com.example.demo.Security.Repositories.RefreshTokenRepository;
import com.example.demo.Security.Repositories.RoleRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(CredentialsRepository credentialsRepository, AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtService jwtService, UserMapper userMapper, RefreshTokenService refreshTokenService, RefreshTokenRepository refreshTokenRepository) {
        this.credentialsRepository = credentialsRepository;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
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

        String accessToken = jwtService.generateToken(credentials);
        String refreshToken = refreshTokenService.createRefreshToken(loginRequest.getEmail()).getToken();

        return new AuthResponse(accessToken, refreshToken);
    }

    @Transactional
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
        user.setNotificationsEnabled(registerRequest.isNotificationsEnabled());
        
        UserEntity savedUser = userRepository.save(user);

        RoleEntity clienteRole = roleRepository.findByRole(Rol.cliente)
                .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado en el sistema"));

        CredentialsEntity credentials = new CredentialsEntity();
        credentials.setEmail(registerRequest.getEmail());
        credentials.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        credentials.setUser(savedUser);
        credentials.setRoles(Set.of(clienteRole));

        CredentialsEntity savedCredentials = credentialsRepository.save(credentials);

        String accessToken = jwtService.generateToken(savedCredentials);
        String refreshToken = refreshTokenService.createRefreshToken(registerRequest.getEmail()).getToken();

        return RegisterResponse.builder()
                .message("Usuario registrado exitosamente")
                .email(savedUser.getEmail())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponse refreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String newAccessToken = jwtService.generateToken(user);
                    return new AuthResponse(newAccessToken, token); // Retorna el mismo o genera uno nuevo
                })
                .orElseThrow(() -> new RuntimeException("Refresh token no encontrado"));
    }

    public void logout(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token no puede estar vacío");
        }
        jwtService.blacklistToken(token);
    }
}