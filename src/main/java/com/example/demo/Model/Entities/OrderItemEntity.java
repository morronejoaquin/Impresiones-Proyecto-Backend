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

    private FileTypeEnum fileType;

    private int copies;
    private double amount;

    private Integer imageWidth;
    private Integer imageHeight;

    private boolean deleted = false;
}

