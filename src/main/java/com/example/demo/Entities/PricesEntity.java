package com.example.demo.Entities;
import jakarta.persistence.*;
import lombok.*;
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
}
