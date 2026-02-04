package com.example.demo.Model.Entities;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricesEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private double pricePerSheetBW;

    @Column(nullable = false)
    private double pricePerSheetColor;

    @Column(nullable = false)
    private double priceRingedBinding;

    @Column(nullable = false)
    private Instant validFrom;

    private Instant validTo;
}
