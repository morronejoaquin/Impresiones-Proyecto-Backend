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

    private double pricePerSheetBW;
    private double pricePerSheetColor;
    private double priceRingedBinding;

    private Instant validFrom;
    private Instant validTo;
}
