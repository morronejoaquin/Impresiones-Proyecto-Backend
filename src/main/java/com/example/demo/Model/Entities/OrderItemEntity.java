package com.example.demo.Model.Entities;
import com.example.demo.Model.Enums.FileTypeEnum;
import lombok.*;

import com.example.demo.Model.Enums.BindingTypeEnum;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "cart_id", nullable = false)
    private CartEntity cart;

    @Column(nullable = false)
    private boolean color;

    @Column(nullable = false)
    private boolean doubleSided;

    @Enumerated(EnumType.STRING)
    private BindingTypeEnum binding;

    @Column(nullable = false)
    private int pages;

    @Column(length = 500)
    private String comments;

    @Column(nullable = false)
    private String driveFileId;

    @Column(nullable = false)
    private String fileName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileTypeEnum fileType;

    @Column(nullable = false)
    private int copies;

    @Column(nullable = false)
    private double amount;

    @Column(nullable = false)
    private double pricePerSheet;

    @Column(nullable = false)
    private double bindingPrice;

    private Integer imageWidth;
    private Integer imageHeight;

    @Column(nullable = false)
    private boolean deleted = false;
}

