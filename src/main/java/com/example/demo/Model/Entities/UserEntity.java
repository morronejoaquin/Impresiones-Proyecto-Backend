package com.example.demo.Model.Entities;
import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class UserEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String surname;

    @Column(unique = true)
    private String email;

    private String phone;
}

