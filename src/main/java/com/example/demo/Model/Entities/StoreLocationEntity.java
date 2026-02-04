package com.example.demo.Model.Entities;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreLocationEntity {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private double lat;

    @Column(nullable = false)
    private double lng;
    
    @Column(nullable = false, length = 200)
    private String address;
}
