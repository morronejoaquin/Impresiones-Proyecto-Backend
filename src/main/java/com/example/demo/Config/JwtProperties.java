package com.example.demo.Config;

import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Value;

import lombok.Getter;

@Component
@Getter
public class JwtProperties {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;
}
