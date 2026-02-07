package com.example.demo.Model.DTOS.Response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class NotificationResponse {
    private UUID id;
    private String message;
    private Instant createdAt;
}
