package com.example.demo.Model.DTOS.Response;

import lombok.Data;

import java.util.UUID;

@Data
public class StoreLocationResponse {
    private UUID id;
    private double lat;
    private double lng;
    private String address;
}
