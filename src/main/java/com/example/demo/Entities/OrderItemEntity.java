package com.example.demo.Entities;
import lombok.*;

import com.example.demo.Enums.BindingTypeEnum;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private CartEntity cart;

    private boolean isColor;
    private boolean isDoubleSided;

    @Enumerated(EnumType.STRING)
    private BindingTypeEnum binding;

    private int pages;

    @Column(length = 500)
    private String comments;

    private String file;

    private int copies;
    private double amount;

    private Integer imageWidth;
    private Integer imageHeight;
}

