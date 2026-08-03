package com.example.demo.Security.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 30, message = "El nombre no puede superar los 30 caracteres")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 30, message = "El apellido no puede superar los 30 caracteres")
    private String surname;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un email válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 18, message = "La contraseña debe tener entre 8 y 18 caracteres")
    @Pattern(
            regexp = "^(?=.*[a-zñ])(?=.*[A-ZÑ])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-zñA-ZÑ\\d@$!%*?&]{8,}$",
            message = "La contraseña debe contener al menos una minúscula, una mayúscula, un número y un carácter especial"
    )
    private String password;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]*$", message = "El teléfono solo debe contener números")
    @Size(max = 15, message = "El teléfono no puede superar los 15 caracteres")
    private String phone;

    private boolean notificationsEnabled;

}
