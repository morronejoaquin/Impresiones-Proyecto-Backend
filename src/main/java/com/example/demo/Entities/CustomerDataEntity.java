package com.example.demo.Entities;
import lombok.*;
import jakarta.persistence.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDataEntity {
    private String name;
    private String surname;
    private String phone;
}

