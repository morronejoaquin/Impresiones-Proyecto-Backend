package com.example.demo.Model.Entities;
import lombok.*;
import jakarta.persistence.*;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDataEntity {

    @Column(length = 100)
    private String name;

    @Column(length = 100)
    private String surname;

    @Column(length = 100)
    private String email;

    @Column(length = 30)
    private String phone;
}

