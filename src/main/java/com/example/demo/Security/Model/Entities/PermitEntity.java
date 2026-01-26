package com.example.demo.Security.Model.Entities;

import com.example.demo.Security.Model.Enums.Permits;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class PermitEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    Permits permit;

    public PermitEntity(Permits permit) {
        this.permit = permit;
    }
}
