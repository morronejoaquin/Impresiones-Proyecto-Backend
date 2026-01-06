package com.example.demo.Model.Entities;
import lombok.*;
import com.example.demo.Model.Enums.UserRoleEnum;
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

    @Column(unique = true)
    private String username;

    private String name;
    private String surname;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserRoleEnum role;

    private String phone;
    private String password;
}

