package com.example.demo.Config;

import org.springframework.stereotype.Component;

import com.google.api.client.util.Value;

import lombok.Getter;

@Component
@Getter
public class JwtProperties {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;
}
