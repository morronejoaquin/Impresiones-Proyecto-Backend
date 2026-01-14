package com.example.demo.Model.Entities;

import java.util.UUID;

import org.springframework.data.annotation.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

@Entity
public class StoreLocationEntity {
    @Id @GeneratedValue
    private UUID id;

    private double lat;
    private double lng;
    
    @Column(length = 200)

    private String address;
}
