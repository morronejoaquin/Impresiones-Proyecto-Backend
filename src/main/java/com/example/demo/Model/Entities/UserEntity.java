package com.example.demo.Model.Entities;
import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Column(nullable = false, length = 100)
    @NotBlank(message = "El apellido es obligatorio")
    private String surname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 30)
    @Pattern(regexp = "^\\d+$", message = "El teléfono solo debe contener números")
    private String phone;
}